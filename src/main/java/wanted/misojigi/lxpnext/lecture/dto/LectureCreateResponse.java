package wanted.misojigi.lxpnext.lecture.dto;

import wanted.misojigi.lxpnext.lecture.domain.Lecture;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;

public record LectureCreateResponse(
	Long lectureId,
	Long instructorId,
	String title,
	String description,
	LectureStatus status
) {

	public static LectureCreateResponse of(Lecture lecture) {
		return new LectureCreateResponse(
			lecture.getId(),
			lecture.getInstructorId(),
			lecture.getTitle(),
			lecture.getDescription(),
			lecture.getStatus()
		);
	}
}
