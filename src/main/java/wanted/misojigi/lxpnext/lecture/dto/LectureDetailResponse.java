package wanted.misojigi.lxpnext.lecture.dto;

import wanted.misojigi.lxpnext.lecture.domain.Lecture;

import java.util.List;

public record LectureDetailResponse(
	Long lectureId,
	Long instructorId,
	String title,
	String description,
	List<ContentResponse> contents
) {

	public static LectureDetailResponse of(
		Lecture lecture,
		List<ContentResponse> contents
	) {
		return new LectureDetailResponse(
			lecture.getId(),
			lecture.getInstructorId(),
			lecture.getTitle(),
			lecture.getDescription(),
			contents
		);
	}
}