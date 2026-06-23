package wanted.misojigi.lxpnext.goal.dto;

import java.time.LocalDateTime;
import java.util.List;
import wanted.misojigi.lxpnext.goal.domain.DetailGoal;
import wanted.misojigi.lxpnext.goal.domain.LearningGoal;


public record GoalResponse(
        Long learningGoalId,
        String title,
        LocalDateTime createdAt,
        List<DetailGoalItem> detailGoals
) {

    public static GoalResponse of(LearningGoal learningGoal, List<DetailGoal> detailGoals) {
        return new GoalResponse(
                learningGoal.getLearningGoalId(),
                learningGoal.getTitle(),
                learningGoal.getCreatedAt(),
                detailGoals.stream()
                        .map(DetailGoalItem::from)
                        .toList()
        );
    }

    public record DetailGoalItem(
            Long detailGoalId,
            String content,
            boolean completed,
            int sortOrder
    ) {

        public static DetailGoalItem from(DetailGoal detailGoal) {
            return new DetailGoalItem(
                    detailGoal.getDetailGoalId(),
                    detailGoal.getContent(),
                    detailGoal.isCompleted(),
                    detailGoal.getSortOrder()
            );
        }
    }
}
