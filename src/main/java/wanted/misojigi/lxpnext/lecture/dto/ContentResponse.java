package wanted.misojigi.lxpnext.lecture.dto;

import wanted.misojigi.lxpnext.lecture.domain.Content;

public record ContentResponse(
	Long contentId,
	String title,
	String contentUrl,
	Integer sortOrder
) {

	public static ContentResponse from(Content content) {
		return new ContentResponse(
			content.getId(),
			content.getTitle(),
			content.getContentUrl(),
			content.getSortOrder()
		);
	}
}