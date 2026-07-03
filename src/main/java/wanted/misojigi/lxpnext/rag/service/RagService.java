package wanted.misojigi.lxpnext.rag.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.lecture.domain.Content;
import wanted.misojigi.lxpnext.lecture.repository.ContentRepository;
import wanted.misojigi.lxpnext.rag.dto.RagAnswerResponse;
import wanted.misojigi.lxpnext.rag.dto.RagReferenceResponse;

import java.util.List;

@Service
public class RagService {

    private final ContentRepository contentRepository;

    public RagService(ContentRepository contentRepository) {
        this.contentRepository = contentRepository;
    }

    @Transactional(readOnly = true)
    public RagAnswerResponse answer(Long lectureId, String question) {

        List<Content> contents = contentRepository.findByLectureIdOrderBySortOrderAscIdAsc(lectureId);

        List<RagReferenceResponse> references = contents.stream()
                .map(content -> new RagReferenceResponse(
                        content.getId(),
                        content.getTitle()
                ))
                .toList();

        String answer = "현재는 AI 답변 연결 전입니다. "
                + "lectureId=" + lectureId
                + ", question=" + question
                + ", 조회된 강의 콘텐츠 수=" + contents.size();

        return new RagAnswerResponse(answer, references);
    }
}