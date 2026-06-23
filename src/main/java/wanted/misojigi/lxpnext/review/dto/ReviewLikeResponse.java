package wanted.misojigi.lxpnext.review.dto;

public record ReviewLikeResponse(
	Long reviewId,
	boolean liked,
	long likeCount
) {

	public static ReviewLikeResponse of(
		Long reviewId,
		boolean liked,
		long likeCount
	) {
		return new ReviewLikeResponse(
			reviewId,
			liked,
			likeCount
		);
	}
}