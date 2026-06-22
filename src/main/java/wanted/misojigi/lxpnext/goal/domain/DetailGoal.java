package wanted.misojigi.lxpnext.goal.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
