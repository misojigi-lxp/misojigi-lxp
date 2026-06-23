package wanted.misojigi.lxpnext.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import wanted.misojigi.lxpnext.common.domain.BaseEntity;

@Entity
@Table(
	name = "review_likes",
	uniqueConstraints = @UniqueConstraint(
		name = "uk_review_like_review_member",
		columnNames = {"review_id", "member_id"}
	)
)
public class ReviewLike extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_like_id")
	private Long id;

	@Column(name = "review_id", nullable = false)
	private Long reviewId;

	@Column(name = "member_id", nullable = false)
	private Long memberId;

	protected ReviewLike() {
	}

	private ReviewLike(Long reviewId, Long memberId) {
		this.reviewId = reviewId;
		this.memberId = memberId;
	}

	public static ReviewLike create(Long reviewId, Long memberId) {
		return new ReviewLike(reviewId, memberId);
	}

	public Long getId() {
		return id;
	}

	public Long getReviewId() {
		return reviewId;
	}

	public Long getMemberId() {
		return memberId;
	}
}