package wanted.misojigi.lxpnext.rag.dto;

public class RagReferenceResponse {

    private Long contentId;
    private String title;

    public RagReferenceResponse(Long contentId, String title) {
        this.contentId = contentId;
        this.title = title;
    }

    public Long getContentId() {
        return contentId;
    }

    public String getTitle() {
        return title;
    }
}