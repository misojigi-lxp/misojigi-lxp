package wanted.misojigi.lxpnext.review.service;

import java.util.List;

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
import wanted.misojigi.lxpnext.review.domain.ReviewStatus;
import wanted.misojigi.lxpnext.review.dto.ReviewCreateRequest;
import wanted.misojigi.lxpnext.review.dto.ReviewListResponse;
import wanted.misojigi.lxpnext.review.dto.ReviewResponse;
import wanted.misojigi.lxpnext.review.repository.ReviewRepository;

@Service
@Transactional
public class ReviewService {

	private final ReviewRepository reviewRepository;
	private final LectureRepository lectureRepository;
	private final MemberRepository memberRepository;
	private final EnrollmentRepository enrollmentRepository;

	public ReviewService(
		ReviewRepository reviewRepository,
		LectureRepository lectureRepository,
		MemberRepository memberRepository,
		EnrollmentRepository enrollmentRepository
	) {
		this.reviewRepository = reviewRepository;
		this.lectureRepository = lectureRepository;
		this.memberRepository = memberRepository;
		this.enrollmentRepository = enrollmentRepository;
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
	public List<ReviewListResponse> findReviews(Long lectureId) {
		Lecture lecture = lectureRepository.findById(lectureId)
			.orElseThrow(() -> new BusinessException(ErrorCode.LECTURE_NOT_FOUND));

		if (lecture.getStatus() != LectureStatus.PUBLIC) {
			throw new BusinessException(ErrorCode.LECTURE_NOT_ACCESSIBLE);
		}

		return reviewRepository
			.findByLectureIdAndStatusOrderByCreatedAtDescIdDesc(
				lectureId,
				ReviewStatus.ACTIVE
			)
			.stream()
			.map(ReviewListResponse::from)
			.toList();
	}
}