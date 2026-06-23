package wanted.misojigi.lxpnext.question.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import wanted.misojigi.lxpnext.common.auth.LoginMember;
import wanted.misojigi.lxpnext.common.auth.SessionConst;
import wanted.misojigi.lxpnext.question.dto.QuestionCreateRequest;
import wanted.misojigi.lxpnext.question.dto.QuestionDetailResponse;
import wanted.misojigi.lxpnext.question.dto.QuestionListResponse;
import wanted.misojigi.lxpnext.question.dto.QuestionUpdateRequest;
import wanted.misojigi.lxpnext.question.service.QuestionService;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuestionListResponse> getQuestions(
            @RequestParam Long lectureId,
            HttpServletRequest request
    ) {
        return questionService.getQuestions(lectureId, extractMemberId(request));
    }

    @GetMapping("/{questionId}")
    public QuestionDetailResponse getQuestion(
            @PathVariable Long questionId,
            HttpServletRequest request
    ) {
        return questionService.getQuestion(questionId, extractMemberId(request));
    }

    // TODO: @LoginMemberOptional 도입 후 교체 예정 (선택적 인증 공통화)
    private Long extractMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return (Long) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
    }

    @PostMapping
    public ResponseEntity<Long> createQuestion(@LoginMember Long memberId, @RequestBody
            QuestionCreateRequest request) {
        Long questionId = questionService.createQuestion(memberId, request);
        return ResponseEntity.ok(questionId);
    }

    @PatchMapping("/{questionId}")
    public ResponseEntity<Long> updateQuestion(@LoginMember Long memberId, @PathVariable Long questionId, @RequestBody
            QuestionUpdateRequest request) {
        questionService.updateQuestion(memberId, questionId, request);
        return ResponseEntity.ok(questionId);
    }
}
