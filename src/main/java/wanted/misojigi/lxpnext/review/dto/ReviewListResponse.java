package wanted.misojigi.lxpnext.review.dto;

import java.time.LocalDateTime;

import wanted.misojigi.lxpnext.review.domain.Review;

public record ReviewListResponse(
	Long reviewId,
	Long lectureId,
	Long writerId,
	String content,
	Integer rating,
	LocalDateTime createdAt,
	boolean likedByMe,
	long likeCount
) {

	public static ReviewListResponse from(Review review) {
		return new ReviewListResponse(
			review.getId(),
			review.getLectureId(),
			review.getWriterId(),
			review.getContent(),
			review.getRating(),
			review.getCreatedAt(),
			false,
			0L
		);
	}

	public static ReviewListResponse of(
		Review review,
		boolean likedByMe,
		long likeCount
	) {
		return new ReviewListResponse(
			review.getId(),
			review.getLectureId(),
			review.getWriterId(),
			review.getContent(),
			review.getRating(),
			review.getCreatedAt(),
			likedByMe,
			likeCount
		);
	}
}