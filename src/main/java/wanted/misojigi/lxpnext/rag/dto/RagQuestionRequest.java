package wanted.misojigi.lxpnext.rag.dto;

import jakarta.validation.constraints.NotBlank;

public class RagQuestionRequest {

    @NotBlank
    private String question;

    protected RagQuestionRequest() {
    }

    public RagQuestionRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }
}