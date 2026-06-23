package wanted.misojigi.lxpnext.question.dto;

import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.question.domain.QuestionVisibility;

public record QuestionCreateRequest(
        Long lectureId,
        String title,
        String content,
        Boolean isPublic
) {
    public QuestionVisibility toVisibility() {
        if (isPublic == null) {
            throw new BusinessException(ErrorCode.COMMON_INVALID_INPUT);
        }
        return isPublic ? QuestionVisibility.PUBLIC : QuestionVisibility.PRIVATE;
    }
}
