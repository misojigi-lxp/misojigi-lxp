package wanted.misojigi.lxpnext.review.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import wanted.misojigi.lxpnext.review.domain.Review;
import wanted.misojigi.lxpnext.review.domain.ReviewStatus;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findByLectureIdAndStatusOrderByCreatedAtDescIdDesc(
		Long lectureId,
		ReviewStatus status
	);
}