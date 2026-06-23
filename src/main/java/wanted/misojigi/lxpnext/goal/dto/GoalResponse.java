package wanted.misojigi.lxpnext.goal.dto;

import java.time.LocalDateTime;
import java.util.List;
import wanted.misojigi.lxpnext.goal.domain.DetailGoal;
import wanted.misojigi.lxpnext.goal.domain.LearningGoal;

/**
 * 학습목표 조회 응답.
 *
 * <p>큰 제목(학습목표)과 그에 속한 세부목표(체크박스 리스트)를 함께 내려준다.
 * 등록 응답과 달리 세부목표의 완료 여부({@code completed})를 포함한다.
 */
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
