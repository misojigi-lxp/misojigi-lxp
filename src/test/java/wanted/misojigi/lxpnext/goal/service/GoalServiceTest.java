package wanted.misojigi.lxpnext.goal.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

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

    private Member activeMember() {
        return Member.create("student_jung", "$2a$10$hashedhashedhashedhashed", "정수강");
    }
}
