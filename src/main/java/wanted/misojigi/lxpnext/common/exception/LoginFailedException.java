package wanted.misojigi.lxpnext.common.exception;

/**
 * 로그인 인증 실패. (HTTP 401)
 *
 * <p>회원 없음 / 비밀번호 불일치 / 탈퇴 회원을 구분하지 않고 동일한 메시지를 사용한다.
 * (계정 열거 공격 방지 — 어느 쪽이 틀렸는지 노출하지 않는다.)
 */
public class LoginFailedException extends RuntimeException {
    public LoginFailedException() {
        super("로그인 정보가 일치하지 않습니다.");
    }
}
