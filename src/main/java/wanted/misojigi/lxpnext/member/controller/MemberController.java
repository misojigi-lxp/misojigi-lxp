package wanted.misojigi.lxpnext.member.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.misojigi.lxpnext.common.auth.SessionConst;
import wanted.misojigi.lxpnext.member.domain.Member;
import wanted.misojigi.lxpnext.member.dto.MemberResponse;
import wanted.misojigi.lxpnext.member.dto.SignupRequest;
import wanted.misojigi.lxpnext.member.service.MemberService;

/**
 * 회원 API.
 *
 * <ul>
 *   <li>POST /members  회원가입 (성공 시 자동 로그인)</li>
 * </ul>
 */
@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * 회원가입. 성공 시 로그인 상태로 만들고(세션 생성) 회원 정보를 반환한다.
     */
    @PostMapping
    public ResponseEntity<MemberResponse> signup(@Valid @RequestBody SignupRequest request,
                                                 HttpServletRequest httpRequest) {
        Member member = memberService.signup(request);
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(SessionConst.LOGIN_MEMBER_ID, member.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(MemberResponse.from(member));
    }
}
