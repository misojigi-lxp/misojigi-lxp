package wanted.misojigi.lxpnext.common.exception;

/**
 * 이미 사용 중인 아이디로 회원가입을 시도했을 때 발생. (HTTP 409)
 * soft delete 특성상 탈퇴한 회원의 아이디도 재사용할 수 없다.
 */
public class DuplicateLoginIdException extends RuntimeException {
    public DuplicateLoginIdException() {
        super("사용 불가능한 아이디입니다.");
    }
}
