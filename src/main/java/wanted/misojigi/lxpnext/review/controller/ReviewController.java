package wanted.misojigi.lxpnext.review.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import wanted.misojigi.lxpnext.common.auth.SessionConst;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.review.dto.ReviewCreateRequest;
import wanted.misojigi.lxpnext.review.dto.ReviewListResponse;
import wanted.misojigi.lxpnext.review.dto.ReviewResponse;
import wanted.misojigi.lxpnext.review.service.ReviewService;

@RestController
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@PostMapping("/lectures/{lectureId}/reviews")
	public ResponseEntity<ReviewResponse> createReview(
		@PathVariable Long lectureId,
		@SessionAttribute(name = SessionConst.LOGIN_MEMBER_ID, required = false) Long loginMemberId,
		@Valid @RequestBody ReviewCreateRequest request
	) {
		if (loginMemberId == null) {
			throw new BusinessException(ErrorCode.MEMBER_LOGIN_REQUIRED);
		}

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(reviewService.createReview(lectureId, loginMemberId, request));
	}

	@GetMapping("/lectures/{lectureId}/reviews")
	public ResponseEntity<List<ReviewListResponse>> findReviews(
		@PathVariable Long lectureId,
		@SessionAttribute(name = SessionConst.LOGIN_MEMBER_ID, required = false) Long loginMemberId
	) {
		return ResponseEntity.ok(reviewService.findReviews(lectureId, loginMemberId));
	}
}