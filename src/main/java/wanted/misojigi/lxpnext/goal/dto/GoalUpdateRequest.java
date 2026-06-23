package wanted.misojigi.lxpnext.goal.dto;

import java.util.List;

/**
 * 학습목표 수정 요청.
 *
 * <p>세부목표는 수정 후의 "최종 상태"를 통째로 보낸다.
 * <ul>
 *   <li>{@code detailGoalId}가 있으면 → 기존 세부목표 수정 (completed 상태 유지)</li>
 *   <li>{@code detailGoalId}가 null이면 → 신규 세부목표 추가</li>
 *   <li>요청에 빠진 기존 세부목표 → 삭제</li>
 * </ul>
 * 노출 순서(sortOrder)는 보낸 배열 순서대로 서버가 다시 부여한다.
 */
public record GoalUpdateRequest(
        String title,
        List<DetailGoalItem> detailGoals
) {

    public record DetailGoalItem(
            Long detailGoalId,
            String content
    ) {
    }
}
