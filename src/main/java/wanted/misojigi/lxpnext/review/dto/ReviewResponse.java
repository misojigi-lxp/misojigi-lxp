package wanted.misojigi.lxpnext.review.dto;

import wanted.misojigi.lxpnext.review.domain.Review;

import java.time.LocalDateTime;

public record ReviewResponse(
	Long reviewId,
	Long lectureId,
	Long writerId,
	String content,
	Integer rating,
	LocalDateTime createdAt
) {

	public static ReviewResponse from(Review review) {
		return new ReviewResponse(
			review.getId(),
			review.getLectureId(),
			review.getWriterId(),
			review.getContent(),
			review.getRating(),
			review.getCreatedAt()
		);
	}
}