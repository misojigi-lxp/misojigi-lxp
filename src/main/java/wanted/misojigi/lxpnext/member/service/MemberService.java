package wanted.misojigi.lxpnext.member.service;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.member.domain.Member;
import wanted.misojigi.lxpnext.member.domain.MemberStatus;
import wanted.misojigi.lxpnext.member.dto.LoginRequest;
import wanted.misojigi.lxpnext.member.dto.SignupRequest;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

/**
 * 회원 도메인 서비스.
 *
 * <p>검증 책임 분리:
 * <ul>
 *   <li>형식 검증(길이·정규식) → DTO @Valid (컨트롤러 진입 전)</li>
 *   <li>중복 검증 → 이 서비스의 existsByLoginId</li>
 *   <li>최종 방어선 → DB UNIQUE 제약 (동시 가입 경합 대비)</li>
 * </ul>
 */
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입. 비밀번호를 해시하여 저장하고, 저장된 회원을 반환한다.
     * (세션 생성은 컨트롤러 책임 — 가입 직후 자동 로그인 처리)
     */
    @Transactional
    public Member signup(SignupRequest request) {
        if (memberRepository.existsByLoginId(request.loginId())) {
            throw new BusinessException(ErrorCode.MEMBER_DUPLICATE_LOGIN_ID);
        }
        String passwordHash = passwordEncoder.encode(request.password());
        Member member = Member.create(request.loginId(), passwordHash, request.nickname());
        try {
            return memberRepository.save(member);
        } catch (DataIntegrityViolationException e) {
            // existsByLoginId 통과와 save 사이에 동일 아이디가 먼저 저장된 경우(동시성)
            throw new BusinessException(ErrorCode.MEMBER_DUPLICATE_LOGIN_ID);
        }
    }

    /**
     * 로그인 인증. 성공 시 회원을 반환한다.
     *
     * <p>활성(ACTIVE) 회원만 조회하므로 탈퇴 회원은 자연히 차단된다.
     * 회원 없음·비밀번호 불일치·탈퇴 회원 모두 동일한 예외로 처리한다.
     */
    @Transactional(readOnly = true)
    public Member authenticate(LoginRequest request) {
        Member member = memberRepository
                .findByLoginIdAndStatus(request.loginId(), MemberStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_INVALID_CREDENTIALS));
        if (!passwordEncoder.matches(request.password(), member.getPasswordHash())) {
            throw new BusinessException(ErrorCode.MEMBER_INVALID_CREDENTIALS);
        }
        return member;
    }
}
