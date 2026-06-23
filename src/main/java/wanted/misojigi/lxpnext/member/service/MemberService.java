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


@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 회원가입.
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
     * 회원탈퇴 (soft delete).
     */
    @Transactional
    public void withdraw(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        member.withdraw();
    }

    /**
     * 로그인 인증. 성공 시 회원을 반환한다.
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
