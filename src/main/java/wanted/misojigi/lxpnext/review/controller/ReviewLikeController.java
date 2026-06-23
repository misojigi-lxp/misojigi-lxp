package wanted.misojigi.lxpnext.review.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import wanted.misojigi.lxpnext.common.auth.SessionConst;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
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
		@SessionAttribute(name = SessionConst.LOGIN_MEMBER_ID, required = false) Long loginMemberId
	) {
		if (loginMemberId == null) {
			throw new BusinessException(ErrorCode.MEMBER_LOGIN_REQUIRED);
		}

		return ResponseEntity.ok(reviewLikeService.createReviewLike(reviewId, loginMemberId));
	}
}