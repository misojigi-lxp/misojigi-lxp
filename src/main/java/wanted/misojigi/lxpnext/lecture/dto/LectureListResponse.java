package wanted.misojigi.lxpnext.lecture.dto;

import wanted.misojigi.lxpnext.lecture.domain.Lecture;

public record LectureListResponse(
	Long lectureId,
	Long instructorId,
	String title,
	String description
) {

	public static LectureListResponse from(Lecture lecture) {
		return new LectureListResponse(
			lecture.getId(),
			lecture.getInstructorId(),
			lecture.getTitle(),
			lecture.getDescription()
		);
	}
}