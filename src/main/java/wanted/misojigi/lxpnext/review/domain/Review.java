package wanted.misojigi.lxpnext.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import wanted.misojigi.lxpnext.common.domain.BaseEntity;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;

@Entity
@Table(name = "reviews")
public class Review extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "review_id")
	private Long id;

	@Column(name = "lecture_id", nullable = false)
	private Long lectureId;

	@Column(name = "writer_id", nullable = false)
	private Long writerId;

	@Column(nullable = false, length = 50)
	private String content;

	@Column(nullable = false)
	private Integer rating;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReviewStatus status;

	protected Review() {
	}

	private Review(
		Long lectureId,
		Long writerId,
		String content,
		Integer rating
	) {
		validateContent(content);
		validateRating(rating);

		this.lectureId = lectureId;
		this.writerId = writerId;
		this.content = content;
		this.rating = rating;
		this.status = ReviewStatus.ACTIVE;
	}

	public static Review create(
		Long lectureId,
		Long writerId,
		String content,
		Integer rating
	) {
		return new Review(lectureId, writerId, content, rating);
	}

	private static void validateContent(String content) {
		if (content == null || content.isBlank()) {
			throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT);
		}

		if (content.length() >= 50) {
			throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT);
		}
	}

	private static void validateRating(Integer rating) {
		if (rating == null || rating < 1 || rating > 5) {
			throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT);
		}
	}

	public Long getId() {
		return id;
	}

	public Long getLectureId() {
		return lectureId;
	}

	public Long getWriterId() {
		return writerId;
	}

	public String getContent() {
		return content;
	}

	public Integer getRating() {
		return rating;
	}

	public ReviewStatus getStatus() {
		return status;
	}
}