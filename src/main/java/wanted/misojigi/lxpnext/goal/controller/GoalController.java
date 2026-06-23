package wanted.misojigi.lxpnext.goal.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wanted.misojigi.lxpnext.common.auth.LoginMember;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateRequest;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateResponse;
import wanted.misojigi.lxpnext.goal.dto.GoalResponse;
import wanted.misojigi.lxpnext.goal.dto.GoalUpdateRequest;
import wanted.misojigi.lxpnext.goal.service.GoalService;

@RestController
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping
    public ResponseEntity<GoalCreateResponse> createGoal(
            @LoginMember Long memberId,
            @RequestBody GoalCreateRequest request
    ) {
        return ResponseEntity.ok(goalService.createGoal(memberId, request));
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> findTodayGoals(
            @LoginMember Long memberId
    ) {
        return ResponseEntity.ok(goalService.findTodayGoals(memberId));
    }

    @PatchMapping("/{goalId}")
    public ResponseEntity<GoalResponse> updateGoal(
            @LoginMember Long memberId,
            @PathVariable Long goalId,
            @RequestBody GoalUpdateRequest request
    ) {
        return ResponseEntity.ok(goalService.updateGoal(memberId, goalId, request));
    }
}
