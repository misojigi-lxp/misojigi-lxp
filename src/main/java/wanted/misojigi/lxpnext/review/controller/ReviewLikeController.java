package wanted.misojigi.lxpnext.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import wanted.misojigi.lxpnext.common.auth.LoginMember;
import wanted.misojigi.lxpnext.review.dto.ReviewLikeResponse;
import wanted.misojigi.lxpnext.review.service.ReviewLikeService;

@RestController
public class ReviewLikeController {

	private final ReviewLikeService reviewLikeService;

	public ReviewLikeController(ReviewLikeService reviewLikeService) {
		this.reviewLikeService = reviewLikeService;
	}

	@PostMapping("/reviews/{reviewId}/likes")
	public ResponseEntity<ReviewLikeResponse> createReviewLike(
		@PathVariable Long reviewId,
		@LoginMember Long loginMemberId
	) {
		return ResponseEntity.ok(reviewLikeService.createReviewLike(reviewId, loginMemberId));
	}
}


