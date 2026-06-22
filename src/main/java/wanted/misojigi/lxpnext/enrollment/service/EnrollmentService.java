package wanted.misojigi.lxpnext.enrollment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.enrollment.domain.Enrollment;
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
}
