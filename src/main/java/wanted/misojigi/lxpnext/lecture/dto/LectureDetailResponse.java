package wanted.misojigi.lxpnext.lecture.dto;

import wanted.misojigi.lxpnext.lecture.domain.Lecture;

import java.util.List;

public record LectureDetailResponse(
	Long lectureId,
	Long instructorId,
	String title,
	String description,
	List<ContentResponse> contents,
	String contentMessage
) {

	private static final String EMPTY_CONTENT_MESSAGE = "등록된 콘텐츠가 없습니다.";

	public static LectureDetailResponse of(
		Lecture lecture,
		List<ContentResponse> contents
	) {
		return new LectureDetailResponse(
			lecture.getId(),
			lecture.getInstructorId(),
			lecture.getTitle(),
			lecture.getDescription(),
			contents,
			createContentMessage(contents)
		);
	}

	private static String createContentMessage(List<ContentResponse> contents) {
		if (contents.isEmpty()) {
			// 강의 상세 응답에서만 쓰는 안내 문구라서 공통 에러코드에서 제외처리함
			return EMPTY_CONTENT_MESSAGE;
		}

		return null;
	}
}