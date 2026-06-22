package wanted.misojigi.lxpnext.goal.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.goal.domain.DetailGoal;
import wanted.misojigi.lxpnext.goal.domain.LearningGoal;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateRequest;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateResponse;
import wanted.misojigi.lxpnext.goal.repository.DetailGoalRepository;
import wanted.misojigi.lxpnext.goal.repository.LearningGoalRepository;
import wanted.misojigi.lxpnext.member.domain.MemberStatus;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class GoalService {

    private static final int MIN_DETAIL_GOALS = 1;
    private static final int MAX_DETAIL_GOALS = 20;

    private final LearningGoalRepository learningGoalRepository;
    private final DetailGoalRepository detailGoalRepository;
    private final MemberRepository memberRepository;

    public GoalService(
            LearningGoalRepository learningGoalRepository,
            DetailGoalRepository detailGoalRepository,
            MemberRepository memberRepository
    ) {
        this.learningGoalRepository = learningGoalRepository;
        this.detailGoalRepository = detailGoalRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public GoalCreateResponse createGoal(Long memberId, GoalCreateRequest request) {
        validateMember(memberId);
        validateDetailGoalCount(request.detailGoals());

        LearningGoal learningGoal = learningGoalRepository.save(
                LearningGoal.create(memberId, request.title())
        );

        List<DetailGoal> detailGoals = request.detailGoals().stream()
                .map(item -> DetailGoal.create(
                        learningGoal.getLearningGoalId(), item.content(), item.sortOrder()))
                .toList();

        detailGoalRepository.saveAll(detailGoals);  // 묶어서 처리

        return GoalCreateResponse.from(learningGoal, detailGoals);
    }

    private void validateMember(Long memberId) {
        memberRepository.findByMemberIdAndStatus(memberId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않거나 탈퇴한 회원입니다."));
    }

    private void validateDetailGoalCount(List<GoalCreateRequest.DetailGoalItem> detailGoals) {
        if (detailGoals == null || detailGoals.size() < MIN_DETAIL_GOALS) {
            throw new IllegalArgumentException("세부목표는 최소 1개 이상이어야 합니다.");
        }
        if (detailGoals.size() > MAX_DETAIL_GOALS) {
            throw new IllegalArgumentException("세부목표는 최대 20개까지 등록할 수 있습니다.");
        }
    }
}
