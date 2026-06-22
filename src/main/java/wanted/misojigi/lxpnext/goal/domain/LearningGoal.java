package wanted.misojigi.lxpnext.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import wanted.misojigi.lxpnext.common.domain.BaseEntity;

/**
 * 학습목표 Aggregate Root.
 *
 * <p>핵심 불변식
 * <ul>
 *   <li>학습목표는 특정 강의에 종속되지 않는다.</li>
 *   <li>목표 제목은 필수값이며 30자 이하이다.</li>
 *   <li>생성일 기준 24시간이 지나면 만료된다.</li>
 *   <li>작성자 본인만 조회·수정·삭제할 수 있다.</li>
 * </ul>
 */
@Entity
@Table(name = "learning_goals")
public class LearningGoal extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "learning_goal_id")
    private Long learningGoalId;

    @Column(name = "member_id", nullable = false, updatable = false)
    private Long memberId;

    @Column(name = "title", length = 30, nullable = false)
    private String title;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected LearningGoal() {
    }

    private LearningGoal(Long memberId, String title) {
        this.memberId = memberId;
        this.title = title;
    }

    public static LearningGoal create(Long memberId, String title) {
        validateMemberId(memberId);
        validateTitle(title);
        return new LearningGoal(memberId, title);
    }

    public void updateTitle(String newTitle) {
        validateTitle(newTitle);
        this.title = newTitle;
        this.updatedAt = LocalDateTime.now();
    }

    /** 작성자 본인 여부 확인 */
    public boolean isOwnedBy(Long memberId) {
        return this.memberId.equals(memberId);
    }

    /** 생성일 기준 24시간 초과 시 만료 */
    public boolean isExpired() {
        return getCreatedAt().plusHours(24).isBefore(LocalDateTime.now());
    }

    private static void validateMemberId(Long memberId) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId는 null일 수 없습니다.");
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title은 필수값입니다.");
        }
        if (title.length() > 30) {
            throw new IllegalArgumentException("title은 30자 이하이어야 합니다.");
        }
    }

    public Long getLearningGoalId() {
        return learningGoalId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public String getTitle() {
        return title;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
