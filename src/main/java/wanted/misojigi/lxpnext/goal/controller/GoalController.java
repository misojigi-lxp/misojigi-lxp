package wanted.misojigi.lxpnext.goal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateRequest;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateResponse;
import wanted.misojigi.lxpnext.goal.service.GoalService;

@RestController
@RequestMapping("/members/{memberId}/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalCreateResponse> createGoal(
            @PathVariable Long memberId,
            @RequestBody GoalCreateRequest request
    ) {
        return ResponseEntity.ok(goalService.createGoal(memberId, request));
    }
}
