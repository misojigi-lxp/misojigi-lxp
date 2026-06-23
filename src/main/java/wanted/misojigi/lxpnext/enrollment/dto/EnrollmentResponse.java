package wanted.misojigi.lxpnext.enrollment.dto;

import java.time.LocalDateTime;
import wanted.misojigi.lxpnext.enrollment.domain.Enrollment;
import wanted.misojigi.lxpnext.lecture.domain.Lecture;

public record EnrollmentResponse(
        Long enrollmentId,
        Long lectureId,
        String lectureTitle,
        LocalDateTime enrolledAt
) {

    public static EnrollmentResponse of(Enrollment enrollment, Lecture lecture) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getLectureId(),
                lecture != null ? lecture.getTitle() : null,
                enrollment.getEnrolledAt()
        );
    }
}
