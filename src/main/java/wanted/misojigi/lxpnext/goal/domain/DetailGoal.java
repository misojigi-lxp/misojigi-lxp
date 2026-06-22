package wanted.misojigi.lxpnext.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 세부목표 엔티티.
 *
 * <p>핵심 불변식
 * <ul>
 *   <li>세부목표는 반드시 하나의 학습목표에 속해야 한다.</li>
 *   <li>세부목표 내용은 필수값이며 50자 이하이다.</li>
 *   <li>최소 1개 · 최대 20개 제약은 서비스 계층에서 검증한다.</li>
 *   <li>달성 처리 권한(본인 여부) 검증은 서비스 계층에서 수행한다.</li>
 * </ul>
 */
@Entity
@Table(name = "detail_goals")
public class DetailGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_goal_id")
    private Long detailGoalId;

    @Column(name = "learning_goal_id", nullable = false, updatable = false)
    private Long learningGoalId;

    @Column(name = "content", length = 50, nullable = false)
    private String content;

    @Column(name = "completed", nullable = false)
    private boolean completed;

    @Column(name = "sort_order", nullable = false)
    private int sortOrder;

    protected DetailGoal() {
    }

    private DetailGoal(Long learningGoalId, String content, int sortOrder) {
        this.learningGoalId = learningGoalId;
        this.content = content;
        this.completed = false;
        this.sortOrder = sortOrder;
    }

    public static DetailGoal create(Long learningGoalId, String content, int sortOrder) {
        validateLearningGoalId(learningGoalId);
        validateContent(content);
        return new DetailGoal(learningGoalId, content, sortOrder);
    }

    public void complete() {
        this.completed = true;
    }

    private static void validateLearningGoalId(Long learningGoalId) {
        if (learningGoalId == null) {
            throw new IllegalArgumentException("learningGoalId는 null일 수 없습니다.");
        }
    }

    private static void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("content는 필수값입니다.");
        }
        if (content.length() > 50) {
            throw new IllegalArgumentException("content는 50자 이하이어야 합니다.");
        }
    }

    public Long getDetailGoalId() {
        return detailGoalId;
    }

    public Long getLearningGoalId() {
        return learningGoalId;
    }

    public String getContent() {
        return content;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getSortOrder() {
        return sortOrder;
    }
}
