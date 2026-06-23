package wanted.misojigi.lxpnext.review.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import wanted.misojigi.lxpnext.review.domain.ReviewLike;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {

	// 이미 좋아요 눌렀는지 확인
	Optional<ReviewLike> findByReviewIdAndMemberId(Long reviewId, Long memberId);

	// 해당 후기의 좋아요 수 계산
	long countByReviewId(Long reviewId);

	// 후기 목록 조회할 때 여러 후기의 좋아요 정보를 한 번에 조회
	List<ReviewLike> findByReviewIdIn(List<Long> reviewIds);
}