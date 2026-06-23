package wanted.misojigi.lxpnext.question.dto;

import java.time.LocalDateTime;
import wanted.misojigi.lxpnext.question.domain.Question;
import wanted.misojigi.lxpnext.question.domain.QuestionVisibility;

public record QuestionDetailResponse(
        Long questionId,
        String title,
        String content,
        String writerNickname,
        LocalDateTime createdAt,
        QuestionVisibility visibility
) {
    public static QuestionDetailResponse from(Question question, String writerNickname) {
        return new QuestionDetailResponse(
                question.getQuestionId(),
                question.getTitle(),
                question.getContent(),
                writerNickname,
                question.getCreatedAt(),
                question.getVisibility()
        );
    }
}