package wanted.misojigi.lxpnext.review.service;

import org.springframework.dao.DataIntegrityViolationException;
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

		if (reviewLikeRepository.findByReviewIdAndMemberId(reviewId, loginMemberId).isEmpty()) {
			try {
				reviewLikeRepository.saveAndFlush(
					ReviewLike.create(reviewId, loginMemberId)
				);
			} catch (DataIntegrityViolationException ignored) {
				// 동시에 같은 회원이 같은 후기에 좋아요를 누른 경우
				// DB UNIQUE(review_id, member_id) 제약으로 중복 저장은 막고,
				// 아래에서 현재 좋아요 수만 다시 계산한다.
			}
		}

		long likeCount = reviewLikeRepository.countByReviewId(reviewId);

		return ReviewLikeResponse.of(
			reviewId,
			true,
			likeCount
		);
	}
}



