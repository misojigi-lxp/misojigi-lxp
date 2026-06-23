package wanted.misojigi.lxpnext.goal.service;

import java.util.List;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
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

        List<GoalCreateRequest.DetailGoalItem> items = request.detailGoals();
        List<DetailGoal> detailGoals = IntStream.range(0, items.size())
                .mapToObj(i -> DetailGoal.create(
                        learningGoal.getLearningGoalId(), items.get(i).content(), i + 1))  // 배열 순서로 sortOrder 자동 부여
                .toList();

        detailGoalRepository.saveAll(detailGoals);  // 묶어서 처리

        return GoalCreateResponse.from(learningGoal, detailGoals);
    }

    private void validateMember(Long memberId) {
        memberRepository.findByMemberIdAndStatus(memberId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDetailGoalCount(List<GoalCreateRequest.DetailGoalItem> detailGoals) {
        if (detailGoals == null || detailGoals.size() < MIN_DETAIL_GOALS) {
            throw new BusinessException(ErrorCode.GOAL_DETAIL_REQUIRED);
        }
        if (detailGoals.size() > MAX_DETAIL_GOALS) {
            throw new BusinessException(ErrorCode.GOAL_DETAIL_LIMIT_EXCEEDED);
        }
    }
}
