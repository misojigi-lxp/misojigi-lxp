package wanted.misojigi.lxpnext.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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
import wanted.misojigi.lxpnext.member.domain.Member;
import wanted.misojigi.lxpnext.member.domain.MemberStatus;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("GoalService")
class GoalServiceTest {

    @Mock private LearningGoalRepository learningGoalRepository;
    @Mock private DetailGoalRepository detailGoalRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks private GoalService goalService;

    @Captor private ArgumentCaptor<List<DetailGoal>> detailGoalsCaptor;

    private static final Long MEMBER_ID = 5L;

    @Nested
    @DisplayName("학습목표를 등록할 때")
    class CreateGoal {

        @Test
        @DisplayName("정상 요청이면 학습목표와 세부목표가 저장되고 sortOrder가 배열 순서대로 부여된다")
        void success() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));

            LearningGoal saved = LearningGoal.create(MEMBER_ID, "React 복습");
            ReflectionTestUtils.setField(saved, "learningGoalId", 1L);
            given(learningGoalRepository.save(any(LearningGoal.class))).willReturn(saved);

            GoalCreateRequest request = new GoalCreateRequest("React 복습", List.of(
                    new GoalCreateRequest.DetailGoalItem("useState 다시 보기"),
                    new GoalCreateRequest.DetailGoalItem("useEffect 실습"),
                    new GoalCreateRequest.DetailGoalItem("Custom Hook 만들기")
            ));

            // when
            GoalCreateResponse response = goalService.createGoal(MEMBER_ID, request);

            // then
            then(detailGoalRepository).should().saveAll(detailGoalsCaptor.capture());
            List<DetailGoal> savedDetails = detailGoalsCaptor.getValue();
            assertThat(savedDetails).extracting(DetailGoal::getSortOrder).containsExactly(1, 2, 3);
            assertThat(savedDetails).extracting(DetailGoal::getLearningGoalId).containsOnly(1L);

            assertThat(response.learningGoalId()).isEqualTo(1L);
            assertThat(response.title()).isEqualTo("React 복습");
            assertThat(response.detailGoals()).hasSize(3);
        }

        @Test
        @DisplayName("존재하지 않거나 탈퇴한 회원이면 예외가 발생하고 아무것도 저장하지 않는다")
        void memberNotFound() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.empty());
            GoalCreateRequest request = new GoalCreateRequest("제목", List.of(
                    new GoalCreateRequest.DetailGoalItem("내용")));

            // when & then
            assertThatThrownBy(() -> goalService.createGoal(MEMBER_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
            then(learningGoalRepository).should(never()).save(any());
            then(detailGoalRepository).should(never()).saveAll(any());
        }

        @Test
        @DisplayName("세부목표가 비어 있으면 예외가 발생한다")
        void emptyDetailGoals() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            GoalCreateRequest request = new GoalCreateRequest("제목", List.of());

            // when & then
            assertThatThrownBy(() -> goalService.createGoal(MEMBER_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_DETAIL_REQUIRED);
            then(learningGoalRepository).should(never()).save(any());
        }

        @Test
        @DisplayName("세부목표가 20개를 초과하면 예외가 발생한다")
        void tooManyDetailGoals() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            List<GoalCreateRequest.DetailGoalItem> items = IntStream.rangeClosed(1, 21)
                    .mapToObj(i -> new GoalCreateRequest.DetailGoalItem("내용" + i))
                    .toList();
            GoalCreateRequest request = new GoalCreateRequest("제목", items);

            // when & then
            assertThatThrownBy(() -> goalService.createGoal(MEMBER_ID, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_DETAIL_LIMIT_EXCEEDED);
            then(learningGoalRepository).should(never()).save(any());
        }
    }

    @Nested
    @DisplayName("오늘의 목표를 조회할 때")
    class FindTodayGoals {

        @Test
        @DisplayName("만료 안 된 목표와 세부목표가 함께 반환되고, 전부 완료된 목표는 completed가 true다")
        void success() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));

            LearningGoal goal1 = goalWithId(1L, MEMBER_ID, "React 복습");
            LearningGoal goal2 = goalWithId(2L, MEMBER_ID, "알고리즘 풀기");
            given(learningGoalRepository.findByMemberIdAndCreatedAtAfterOrderByCreatedAtAsc(
                    eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(List.of(goal1, goal2));

            given(detailGoalRepository.findByLearningGoalIdInOrderBySortOrderAsc(List.of(1L, 2L)))
                    .willReturn(List.of(
                            detailWithId(10L, 1L, "useState", 1, true),
                            detailWithId(11L, 1L, "useEffect", 2, true),
                            detailWithId(12L, 2L, "DP 기초", 1, false)));

            // when
            List<GoalResponse> responses = goalService.findTodayGoals(MEMBER_ID);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).completed()).isTrue();   // goal1: 세부목표 전부 완료
            assertThat(responses.get(0).detailGoals()).hasSize(2);
            assertThat(responses.get(1).completed()).isFalse();  // goal2: 미완료 포함
        }

        @Test
        @DisplayName("만료 안 된 목표가 없으면 빈 리스트를 반환하고 세부목표는 조회하지 않는다")
        void empty() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findByMemberIdAndCreatedAtAfterOrderByCreatedAtAsc(
                    eq(MEMBER_ID), any(LocalDateTime.class)))
                    .willReturn(List.of());

            // when
            List<GoalResponse> responses = goalService.findTodayGoals(MEMBER_ID);

            // then
            assertThat(responses).isEmpty();
            then(detailGoalRepository).should(never()).findByLearningGoalIdInOrderBySortOrderAsc(any());
        }

        @Test
        @DisplayName("존재하지 않거나 탈퇴한 회원이면 예외가 발생한다")
        void memberNotFound() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> goalService.findTodayGoals(MEMBER_ID))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("학습목표를 수정할 때")
    class UpdateGoal {

        @Test
        @DisplayName("제목을 바꾸고 세부목표를 추가·수정·삭제로 동기화하며, 수정된 항목의 완료 상태는 유지된다")
        void success() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            LearningGoal goal = goalWithId(1L, MEMBER_ID, "옛 제목");
            given(learningGoalRepository.findById(1L)).willReturn(Optional.of(goal));

            DetailGoal existing1 = detailWithId(10L, 1L, "기존1", 1, true);   // 완료 상태
            DetailGoal existing2 = detailWithId(11L, 1L, "기존2", 2, false);  // 요청에서 빠짐 → 삭제
            given(detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(1L))
                    .willReturn(List.of(existing1, existing2));

            GoalUpdateRequest request = new GoalUpdateRequest("새 제목", List.of(
                    new GoalUpdateRequest.DetailGoalItem(10L, "기존1 수정"),  // 기존 수정
                    new GoalUpdateRequest.DetailGoalItem(null, "신규")        // 신규 추가
            ));

            // when
            GoalResponse response = goalService.updateGoal(MEMBER_ID, 1L, request);

            // then
            assertThat(goal.getTitle()).isEqualTo("새 제목");
            assertThat(existing1.getContent()).isEqualTo("기존1 수정");
            assertThat(existing1.isCompleted()).isTrue();  // 완료 상태 보존

            then(detailGoalRepository).should().deleteAll(List.of(existing2));  // 빠진 항목 삭제
            then(detailGoalRepository).should().saveAll(any());
            assertThat(response.detailGoals())
                    .extracting(GoalResponse.DetailGoalItem::content)
                    .containsExactly("기존1 수정", "신규");
        }

        @Test
        @DisplayName("본인 목표가 아니면 GOAL_ACCESS_DENIED 예외가 발생한다")
        void accessDenied() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, 999L, "남의 목표")));
            GoalUpdateRequest request = new GoalUpdateRequest("제목", List.of(
                    new GoalUpdateRequest.DetailGoalItem(null, "내용")));

            // when & then
            assertThatThrownBy(() -> goalService.updateGoal(MEMBER_ID, 1L, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_ACCESS_DENIED);
        }

        @Test
        @DisplayName("존재하지 않는 목표면 GOAL_NOT_FOUND 예외가 발생한다")
        void goalNotFound() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(99L)).willReturn(Optional.empty());
            GoalUpdateRequest request = new GoalUpdateRequest("제목", List.of(
                    new GoalUpdateRequest.DetailGoalItem(null, "내용")));

            // when & then
            assertThatThrownBy(() -> goalService.updateGoal(MEMBER_ID, 99L, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_NOT_FOUND);
        }

        @Test
        @DisplayName("요청한 세부목표가 해당 목표 소속이 아니면 GOAL_DETAIL_NOT_FOUND 예외가 발생한다")
        void detailNotFound() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, MEMBER_ID, "제목")));
            given(detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(1L))
                    .willReturn(List.of(detailWithId(10L, 1L, "기존", 1, false)));
            GoalUpdateRequest request = new GoalUpdateRequest("제목", List.of(
                    new GoalUpdateRequest.DetailGoalItem(999L, "엉뚱한 id")));  // 소속 아님

            // when & then
            assertThatThrownBy(() -> goalService.updateGoal(MEMBER_ID, 1L, request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_DETAIL_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("학습목표를 삭제할 때")
    class DeleteGoal {

        @Test
        @DisplayName("목표와 하위 세부목표를 모두 삭제한다")
        void success() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            LearningGoal goal = goalWithId(1L, MEMBER_ID, "삭제할 목표");
            given(learningGoalRepository.findById(1L)).willReturn(Optional.of(goal));
            List<DetailGoal> details = List.of(
                    detailWithId(10L, 1L, "세부1", 1, false),
                    detailWithId(11L, 1L, "세부2", 2, false));
            given(detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(1L)).willReturn(details);

            // when
            goalService.deleteGoal(MEMBER_ID, 1L);

            // then
            then(detailGoalRepository).should().deleteAll(details);
            then(learningGoalRepository).should().delete(goal);
        }

        @Test
        @DisplayName("본인 목표가 아니면 GOAL_ACCESS_DENIED 예외가 발생하고 아무것도 삭제하지 않는다")
        void accessDenied() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, 999L, "남의 목표")));

            // when & then
            assertThatThrownBy(() -> goalService.deleteGoal(MEMBER_ID, 1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_ACCESS_DENIED);
            then(learningGoalRepository).should(never()).delete(any());
        }

        @Test
        @DisplayName("존재하지 않는 목표면 GOAL_NOT_FOUND 예외가 발생한다")
        void goalNotFound() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(99L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> goalService.deleteGoal(MEMBER_ID, 99L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("세부목표 달성 상태를 변경할 때")
    class UpdateDetailGoalCompletion {

        @Test
        @DisplayName("세부목표를 완료 처리하고, 전부 완료되면 응답의 completed가 true다")
        void completeAll() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, MEMBER_ID, "목표")));
            DetailGoal d1 = detailWithId(10L, 1L, "세부1", 1, true);   // 이미 완료
            DetailGoal d2 = detailWithId(11L, 1L, "세부2", 2, false);  // 이번에 완료
            given(detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(1L))
                    .willReturn(List.of(d1, d2));

            // when
            GoalResponse response = goalService.updateDetailGoalCompletion(MEMBER_ID, 1L, 11L, true);

            // then
            assertThat(d2.isCompleted()).isTrue();
            assertThat(response.completed()).isTrue();  // 전부 완료 → 목표 달성
        }

        @Test
        @DisplayName("일부만 완료된 상태면 응답의 completed가 false다")
        void notAllCompleted() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, MEMBER_ID, "목표")));
            DetailGoal d1 = detailWithId(10L, 1L, "세부1", 1, false);
            DetailGoal d2 = detailWithId(11L, 1L, "세부2", 2, false);
            given(detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(1L))
                    .willReturn(List.of(d1, d2));

            // when
            GoalResponse response = goalService.updateDetailGoalCompletion(MEMBER_ID, 1L, 10L, true);

            // then
            assertThat(d1.isCompleted()).isTrue();
            assertThat(response.completed()).isFalse();  // d2가 아직 미완료
        }

        @Test
        @DisplayName("완료된 세부목표를 해제할 수 있다")
        void uncheck() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, MEMBER_ID, "목표")));
            DetailGoal d1 = detailWithId(10L, 1L, "세부1", 1, true);
            given(detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(1L))
                    .willReturn(List.of(d1));

            // when
            GoalResponse response = goalService.updateDetailGoalCompletion(MEMBER_ID, 1L, 10L, false);

            // then
            assertThat(d1.isCompleted()).isFalse();
            assertThat(response.completed()).isFalse();
        }

        @Test
        @DisplayName("본인 목표가 아니면 GOAL_ACCESS_DENIED 예외가 발생한다")
        void accessDenied() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, 999L, "남의 목표")));

            // when & then
            assertThatThrownBy(() -> goalService.updateDetailGoalCompletion(MEMBER_ID, 1L, 10L, true))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_ACCESS_DENIED);
        }

        @Test
        @DisplayName("해당 목표에 없는 세부목표면 GOAL_DETAIL_NOT_FOUND 예외가 발생한다")
        void detailNotFound() {
            // given
            given(memberRepository.findByMemberIdAndStatus(MEMBER_ID, MemberStatus.ACTIVE))
                    .willReturn(Optional.of(activeMember()));
            given(learningGoalRepository.findById(1L))
                    .willReturn(Optional.of(goalWithId(1L, MEMBER_ID, "목표")));
            given(detailGoalRepository.findByLearningGoalIdOrderBySortOrderAsc(1L))
                    .willReturn(List.of(detailWithId(10L, 1L, "세부1", 1, false)));

            // when & then
            assertThatThrownBy(() -> goalService.updateDetailGoalCompletion(MEMBER_ID, 1L, 999L, true))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.GOAL_DETAIL_NOT_FOUND);
        }
    }

    private Member activeMember() {
        return Member.create("student_jung", "$2a$10$hashedhashedhashedhashed", "정수강");
    }

    private LearningGoal goalWithId(Long id, Long memberId, String title) {
        LearningGoal goal = LearningGoal.create(memberId, title);
        ReflectionTestUtils.setField(goal, "learningGoalId", id);
        return goal;
    }

    private DetailGoal detailWithId(Long id, Long goalId, String content, int sortOrder, boolean completed) {
        DetailGoal detail = DetailGoal.create(goalId, content, sortOrder);
        ReflectionTestUtils.setField(detail, "detailGoalId", id);
        if (completed) {
            detail.complete();
        }
        return detail;
    }
}
