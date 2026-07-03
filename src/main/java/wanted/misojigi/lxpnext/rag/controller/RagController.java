package wanted.misojigi.lxpnext.rag.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import wanted.misojigi.lxpnext.rag.dto.RagAnswerResponse;
import wanted.misojigi.lxpnext.rag.dto.RagQuestionRequest;
import wanted.misojigi.lxpnext.rag.service.RagService;

@RestController
@RequestMapping("/lectures/{lectureId}/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping("/questions")
    public ResponseEntity<RagAnswerResponse> ask(
            @PathVariable Long lectureId,
            @Valid @RequestBody RagQuestionRequest request
    ) {
        RagAnswerResponse response = ragService.answer(lectureId, request.getQuestion());
        return ResponseEntity.ok(response);
    }
}