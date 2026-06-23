package wanted.misojigi.lxpnext.question.dto;

import java.time.LocalDateTime;
import wanted.misojigi.lxpnext.question.domain.Question;
import wanted.misojigi.lxpnext.question.domain.QuestionVisibility;

public record QuestionListResponse(
        Long questionId,
        String title,
        String writerNickname,
        LocalDateTime createdAt,
        QuestionVisibility visibility
) {
    public static QuestionListResponse from(Question question, String writerNickname) {
        return new QuestionListResponse(
                question.getQuestionId(),
                question.getTitle(),
                writerNickname,
                question.getCreatedAt(),
                question.getVisibility()
        );
    }
}
