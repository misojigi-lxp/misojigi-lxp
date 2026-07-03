package wanted.misojigi.lxpnext.rag.dto;

import java.util.List;

public class RagAnswerResponse {

    private String answer;
    private List<RagReferenceResponse> references;

    public RagAnswerResponse(String answer, List<RagReferenceResponse> references) {
        this.answer = answer;
        this.references = references;
    }

    public String getAnswer() {
        return answer;
    }

    public List<RagReferenceResponse> getReferences() {
        return references;
    }
}