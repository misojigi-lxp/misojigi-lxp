package wanted.misojigi.lxpnext.enrollment.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.enrollment.domain.Enrollment;
import wanted.misojigi.lxpnext.enrollment.dto.EnrollmentResponse;
import wanted.misojigi.lxpnext.enrollment.repository.EnrollmentRepository;
import wanted.misojigi.lxpnext.lecture.domain.Lecture;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;
import wanted.misojigi.lxpnext.lecture.repository.LectureRepository;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             LectureRepository lectureRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.lectureRepository = lectureRepository;
    }

    /**
     * 수강 신청.
     *
     * <p>검증 순서
     * <ol>
     *   <li>강의 존재 여부 → LECTURE_NOT_FOUND</li>
     *   <li>강의 공개 여부 → LECTURE_NOT_ACCESSIBLE</li>
     *   <li>중복 수강 여부 → ENROLLMENT_ALREADY_EXISTS</li>
     * </ol>
     */
    @Transactional
    public Enrollment enroll(Long memberId, Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

        if (lecture.getStatus() != LectureStatus.PUBLIC) {
            throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
        }

        if (enrollmentRepository.existsByMemberIdAndLectureId(memberId, lectureId)) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        return enrollmentRepository.save(Enrollment.create(memberId, lectureId));
    }

    /**
     * 본인 수강 목록 조회.
     * 강의 정보를 한 번에 batch 조회하여 N+1 방지.
     */
    public List<EnrollmentResponse> getMyEnrollments(Long memberId) {
        List<Enrollment> enrollments =
                enrollmentRepository.findAllByMemberIdOrderByEnrolledAtDesc(memberId);

        if (enrollments.isEmpty()) {
            return List.of();
        }

        List<Long> lectureIds = enrollments.stream()
                .map(Enrollment::getLectureId)
                .toList();

        Map<Long, Lecture> lectureMap = lectureRepository.findAllById(lectureIds)
                .stream()
                .collect(Collectors.toMap(Lecture::getId, l -> l));

        return enrollments.stream()
                .map(e -> EnrollmentResponse.of(e, lectureMap.get(e.getLectureId())))
                .toList();
    }
}
