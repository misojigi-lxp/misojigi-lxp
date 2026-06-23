package wanted.misojigi.lxpnext.review.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;
import wanted.misojigi.lxpnext.review.domain.Review;
import wanted.misojigi.lxpnext.review.domain.ReviewLike;
import wanted.misojigi.lxpnext.review.domain.ReviewStatus;
import wanted.misojigi.lxpnext.review.dto.ReviewLikeResponse;
import wanted.misojigi.lxpnext.review.repository.ReviewLikeRepository;
import wanted.misojigi.lxpnext.review.repository.ReviewRepository;

@Service
@Transactional
public class ReviewLikeService {

	private final ReviewLikeRepository reviewLikeRepository;
	private final ReviewRepository reviewRepository;
	private final MemberRepository memberRepository;

	public ReviewLikeService(
		ReviewLikeRepository reviewLikeRepository,
		ReviewRepository reviewRepository,
		MemberRepository memberRepository
	) {
		this.reviewLikeRepository = reviewLikeRepository;
		this.reviewRepository = reviewRepository;
		this.memberRepository = memberRepository;
	}

	public ReviewLikeResponse createReviewLike(Long reviewId, Long loginMemberId) {
		if (!memberRepository.existsById(loginMemberId)) {
			throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

		if (review.getStatus() != ReviewStatus.ACTIVE) {
			throw new BusinessException(ErrorCode.REVIEW_LIKE_NOT_ALLOWED);
		}

		reviewLikeRepository.findByReviewIdAndMemberId(reviewId, loginMemberId)
			.orElseGet(() -> reviewLikeRepository.save(
				ReviewLike.create(reviewId, loginMemberId)
			));

		long likeCount = reviewLikeRepository.countByReviewId(reviewId);

		return ReviewLikeResponse.of(
			reviewId,
			true,
			likeCount
		);
	}
}