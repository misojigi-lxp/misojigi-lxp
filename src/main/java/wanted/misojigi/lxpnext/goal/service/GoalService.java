package wanted.misojigi.lxpnext.goal.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wanted.misojigi.lxpnext.common.exception.BusinessException;
import wanted.misojigi.lxpnext.common.exception.ErrorCode;
import wanted.misojigi.lxpnext.goal.domain.DetailGoal;
import wanted.misojigi.lxpnext.goal.domain.LearningGoal;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateRequest;
import wanted.misojigi.lxpnext.goal.dto.GoalCreateResponse;
import wanted.misojigi.lxpnext.goal.dto.GoalResponse;
import wanted.misojigi.lxpnext.goal.dto.GoalUpdateRequest;
import wanted.misojigi.lxpnext.goal.repository.DetailGoalRepository;
import wanted.misojigi.lxpnext.goal.repository.LearningGoalRepository;
import wanted.misojigi.lxpnext.member.domain.MemberStatus;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

@Service
@Transactional(readOnly = true)
public class GoalService {

    private static final int MIN_DETAIL_GOALS = 1;
    private static final int MAX_DETAIL_GOALS = 20;
    private static final int EXPIRE_HOURS = 24;

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

    /**
     * 로그인한 회원의 '오늘의 목표'(생성 후 24시간 이내) 목록을 세부목표와 함께 조회한다.
     * 만료되었거나 목표가 없으면 빈 리스트를 반환한다.
     */
    public List<GoalResponse> findTodayGoals(Long memberId) {
        validateMember(memberId);

        LocalDateTime threshold = LocalDateTime.now().minusHours(EXPIRE_HOURS);
        List<LearningGoal> goals =
                learningGoalRepository.findByMemberIdAndCreatedAtAfterOrderByCreatedAtAsc(memberId, threshold);
        if (goals.isEmpty()) {
            return List.of();
        }

        List<Long> goalIds = goals.stream()
                .map(LearningGoal::getLearningGoalId)
                .toList();
        Map<Long, List<DetailGoal>> detailsByGoalId =
                detailGoalRepository.findByLearningGoalIdInOrderBySortOrderAsc(goalIds).stream()
                        .collect(Collectors.groupingBy(DetailGoal::getLearningGoalId));

        return goals.stream()
                .map(goal -> GoalResponse.of(
                        goal, detailsByGoalId.getOrDefault(goal.getLearningGoalId(), List.of())))
                .toList();
    }

    /**
     * 학습목표를 수정한다. 제목을 갱신하고, 세부목표는 요청된 최종 상태로 맞춘다.
     * <ul>
     *   <li>detailGoalId가 있으면 기존 세부목표 수정 (completed 유지)</li>
     *   <li>detailGoalId가 null이면 신규 추가</li>
     *   <li>요청에 빠진 기존 세부목표는 삭제</li>
     * </ul>
     */
    @Transactional
    public GoalResponse updateGoal(Long memberId, Long goalId, GoalUpdateRequest request) {
        validateMember(memberId);
        validateDetailGoalCount(request.detailGoals());

        LearningGoal goal = learningGoalRepository.findById(goalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
        if (!goal.isOwnedBy(memberId)) {
            throw new BusinessException(ErrorCode.GOAL_ACCESS_DENIED);
        }

        goal.updateTitle(request.title());

        List<DetailGoal> result = applyDetailGoalChanges(goalId, request.detailGoals());
        return GoalResponse.of(goal, result);
    }

    /**
     * 학습목표를 삭제한다(soft delete). 하위 세부목표도 함께 삭제된다.
     * 작성자 본인만 삭제할 수 있으며, 달성 여부와 무관하게 언제든 삭제 가능하다.
     */
    @Transactional
    public void deleteGoal(Long memberId, Long goalId) {
        validateMember(memberId);

        LearningGoal goal = learningGoalRepository.findById(goalId)
                .orElseThrow(() -> new BusinessException(ErrorCode.GOAL_NOT_FOUND));
        if (!goal.isOwnedBy(memberId)) {
            throw new BusinessException(ErrorCode.GOAL_ACCESS_DENIED);
        }

        List<DetailGoal> details = detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(goalId);
        detailGoalRepository.deleteAll(details);  // @SQLDelete → 세부목표도 soft delete
        learningGoalRepository.delete(goal);       // @SQLDelete → 학습목표 soft delete
    }

    /** 세부목표를 요청된 최종 상태(추가/수정/삭제)로 동기화하고, 노출 순서대로 정렬된 결과를 반환한다. */
    private List<DetailGoal> applyDetailGoalChanges(
            Long goalId, List<GoalUpdateRequest.DetailGoalItem> items) {
        Map<Long, DetailGoal> existingById =
                detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(goalId).stream()
                        .collect(Collectors.toMap(DetailGoal::getDetailGoalId, Function.identity()));

        List<DetailGoal> result = new ArrayList<>();
        Set<Long> keptIds = new HashSet<>();
        for (int i = 0; i < items.size(); i++) {
            GoalUpdateRequest.DetailGoalItem item = items.get(i);
            int sortOrder = i + 1;  // 배열 순서대로 노출 순서 재부여
            if (item.detailGoalId() == null) {
                result.add(DetailGoal.create(goalId, item.content(), sortOrder));  // 신규
            } else {
                DetailGoal existing = existingById.get(item.detailGoalId());
                if (existing == null) {
                    throw new BusinessException(ErrorCode.GOAL_DETAIL_NOT_FOUND);
                }
                existing.update(item.content(), sortOrder);  // 기존 수정 (completed 유지)
                keptIds.add(existing.getDetailGoalId());
                result.add(existing);
            }
        }

        List<DetailGoal> toDelete = existingById.values().stream()
                .filter(detailGoal -> !keptIds.contains(detailGoal.getDetailGoalId()))
                .toList();
        detailGoalRepository.deleteAll(toDelete);
        detailGoalRepository.saveAll(result);  // 신규는 insert, 기존은 변경감지로 update

        return result;
    }

    private void validateMember(Long memberId) {
        memberRepository.findByMemberIdAndStatus(memberId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }

    private void validateDetailGoalCount(List<?> detailGoals) {
        if (detailGoals == null || detailGoals.size() < MIN_DETAIL_GOALS) {
            throw new BusinessException(ErrorCode.GOAL_DETAIL_REQUIRED);
        }
        if (detailGoals.size() > MAX_DETAIL_GOALS) {
            throw new BusinessException(ErrorCode.GOAL_DETAIL_LIMIT_EXCEEDED);
        }
    }
}
