package wanted.misojigi.lxpnext.goal.dto;

import java.time.LocalDateTime;
import java.util.List;
import wanted.misojigi.lxpnext.goal.domain.DetailGoal;
import wanted.misojigi.lxpnext.goal.domain.LearningGoal;

public record GoalCreateResponse(
        Long learningGoalId,
        String title,
        LocalDateTime createdAt,
        List<DetailGoalItem> detailGoals
) {

    public static GoalCreateResponse from(LearningGoal learningGoal, List<DetailGoal> detailGoals) {
        return new GoalCreateResponse(
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
            int sortOrder
    ) {

        public static DetailGoalItem from(DetailGoal detailGoal) {
            return new DetailGoalItem(
                    detailGoal.getDetailGoalId(),
                    detailGoal.getContent(),
                    detailGoal.getSortOrder()
            );
        }
    }
}
