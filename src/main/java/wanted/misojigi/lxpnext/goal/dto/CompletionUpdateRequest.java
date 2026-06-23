package wanted.misojigi.lxpnext.goal.dto;

/**
 * 세부목표 달성 상태 변경 요청.
 *
 * <p>토글된 "최종 상태"를 보낸다. (true = 완료, false = 미완료)
 * 프론트가 낙관적으로 적용한 상태를 그대로 전달하므로, 같은 요청이 재전송되어도 결과가 동일하다.
 */
public record CompletionUpdateRequest(
        boolean completed
) {
}
