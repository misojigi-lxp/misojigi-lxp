package wanted.misojigi.lxpnext.review.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.enrollment.repository.EnrollmentRepository;
import wanted.misojigi.lxpnext.lecture.domain.Lecture;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;
import wanted.misojigi.lxpnext.lecture.repository.LectureRepository;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;
import wanted.misojigi.lxpnext.review.domain.Review;
import wanted.misojigi.lxpnext.review.domain.ReviewLike;
import wanted.misojigi.lxpnext.review.domain.ReviewStatus;
import wanted.misojigi.lxpnext.review.dto.ReviewCreateRequest;
import wanted.misojigi.lxpnext.review.dto.ReviewListResponse;
import wanted.misojigi.lxpnext.review.dto.ReviewResponse;
import wanted.misojigi.lxpnext.review.repository.ReviewLikeRepository;
import wanted.misojigi.lxpnext.review.repository.ReviewRepository;

@Service
@Transactional
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final LectureRepository lectureRepository;
	private final MemberRepository memberRepository;
	private final EnrollmentRepository enrollmentRepository;
	private final ReviewLikeRepository reviewLikeRepository;

	public ReviewService(
		ReviewRepository reviewRepository,
		LectureRepository lectureRepository,
		MemberRepository memberRepository,
		EnrollmentRepository enrollmentRepository,
		ReviewLikeRepository reviewLikeRepository
	) {
		this.reviewRepository = reviewRepository;
		this.lectureRepository = lectureRepository;
		this.memberRepository = memberRepository;
		this.enrollmentRepository = enrollmentRepository;
		this.reviewLikeRepository = reviewLikeRepository;
	}

	public ReviewResponse createReview(
		Long lectureId,
		Long loginMemberId,
		ReviewCreateRequest request
	) {
		if (!memberRepository.existsById(loginMemberId)) {
			throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
		}

		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

		if (lecture.getStatus() != LectureStatus.PUBLIC) {
			throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
		}

		if (!enrollmentRepository.existsByMemberIdAndLectureId(loginMemberId, lectureId)) {
			throw new BusinessException(ErrorCode.REVIEW_NOT_ENROLLED);
		}

		Review review = Review.create(
			lectureId,
			loginMemberId,
			request.content(),
			request.rating()
		);

		Review savedReview = reviewRepository.save(review);

		return ReviewResponse.from(savedReview);
	}

	@Transactional(readOnly = true)
	public List<ReviewListResponse> findReviews(
		Long lectureId,
		Long loginMemberId
	) {
		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

		if (lecture.getStatus() != LectureStatus.PUBLIC) {
			throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
		}

		List<Review> reviews = reviewRepository
			.findByLectureIdAndStatusOrderByCreatedAtDescIdDesc(
				lectureId,
				ReviewStatus.ACTIVE
			);

		if (reviews.isEmpty()) {
			return List.of();
		}

		List<Long> reviewIds = reviews.stream()
			.map(Review::getId)
			.toList();

		List<ReviewLike> reviewLikes = reviewLikeRepository.findByReviewIdIn(reviewIds);

		Map<Long, Long> likeCountByReviewId = reviewLikes.stream()
			.collect(Collectors.groupingBy(
				ReviewLike::getReviewId,
				Collectors.counting()
			));

		Set<Long> likedReviewIds = reviewLikes.stream()
			.filter(reviewLike -> loginMemberId != null
				&& reviewLike.getMemberId().equals(loginMemberId))
			.map(ReviewLike::getReviewId)
			.collect(Collectors.toSet());

		return reviews.stream()
			.map(review -> ReviewListResponse.of(
				review,
				likedReviewIds.contains(review.getId()),
				likeCountByReviewId.getOrDefault(review.getId(), 0L)
			))
			.toList();
	}
}