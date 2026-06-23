package wanted.misojigi.lxpnext.review.dto;

import java.time.LocalDateTime;

import wanted.misojigi.lxpnext.review.domain.Review;

public record ReviewListResponse(
	Long reviewId,
	Long lectureId,
	Long writerId,
	String content,
	Integer rating,
	LocalDateTime createdAt
) {

	public static ReviewListResponse from(Review review) {
		return new ReviewListResponse(
			review.getId(),
			review.getLectureId(),
			review.getWriterId(),
			review.getContent(),
			review.getRating(),
			review.getCreatedAt()
		);
	}
}