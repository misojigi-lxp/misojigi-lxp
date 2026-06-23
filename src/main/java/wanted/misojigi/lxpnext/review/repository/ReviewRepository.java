package wanted.misojigi.lxpnext.review.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import wanted.misojigi.lxpnext.review.domain.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}