package wanted.misojigi.lxpnext.enrollment.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
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
import wanted.misojigi.lxpnext.member.domain.MemberStatus;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    public EnrollmentService(
        EnrollmentRepository enrollmentRepository,
        LectureRepository lectureRepository,
        MemberRepository memberRepository
    ) {
        this.enrollmentRepository = enrollmentRepository;
        this.lectureRepository = lectureRepository;
        this.memberRepository = memberRepository;
    }

    /**
     * 수강 신청.
     *
     * <p>검증 규칙
     * <ul>
     *   <li>활성 회원만 수강 신청할 수 있다.</li>
     *   <li>존재하지 않는 강의는 신청할 수 없다.</li>
     *   <li>PUBLIC 상태의 강의만 신청할 수 있다.</li>
     *   <li>동일 회원은 동일 강의를 중복 신청할 수 없다.</li>
     * </ul>
     */
    @Transactional
    public Enrollment enroll(Long memberId, Long lectureId) {
        validateActiveMember(memberId);

        Lecture lecture = lectureRepository.findById(lectureId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_LECTURE_NOT_ACCESSIBLE));

        if (lecture.getStatus() != LectureStatus.PUBLIC) {
            throw new BusinessException(ErrorCode.ENROLLMENT_LECTURE_NOT_ACCESSIBLE);
        }

        if (enrollmentRepository.existsByMemberIdAndLectureId(memberId, lectureId)) {
            throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
        }

        try {
            return enrollmentRepository.save(Enrollment.create(memberId, lectureId));
        } catch (DataIntegrityViolationException e) {
            if (isDuplicateEnrollmentException(e)) {
                throw new BusinessException(ErrorCode.ENROLLMENT_ALREADY_EXISTS);
            }

            throw e;
        }
    }

    /**
     * 본인 수강 전체 조회.
     *
     * <p>활성 회원만 본인 수강 목록을 조회할 수 있다.
     */
    public List<EnrollmentResponse> getMyEnrollments(Long memberId) {
        validateActiveMember(memberId);

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
            .collect(Collectors.toMap(Lecture::getId, lecture -> lecture));

        return enrollments.stream()
            .map(enrollment -> EnrollmentResponse.of(
                enrollment,
                lectureMap.get(enrollment.getLectureId())
            ))
            .toList();
    }

    private void validateActiveMember(Long memberId) {
        memberRepository.findByMemberIdAndStatus(memberId, MemberStatus.ACTIVE)
            .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private boolean isDuplicateEnrollmentException(DataIntegrityViolationException e) {
        String message = e.getMostSpecificCause().getMessage();

        if (message == null) {
            return false;
        }

        String lowerMessage = message.toLowerCase();

        return lowerMessage.contains("enrollment")
            && lowerMessage.contains("member")
            && lowerMessage.contains("lecture");
    }
}