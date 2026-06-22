package wanted.misojigi.lxpnext.common.exception;

/**
 * 로그인이 필요한 기능에 비로그인 상태로 접근했을 때 발생. (HTTP 401)
 * 세션 만료·미로그인 모두 이 예외로 처리한다.
 */
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("로그인이 필요합니다.");
    }
}
