package wanted.misojigi.lxpnext.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

@DisplayName("LearningGoal 도메인")
class LearningGoalTest {

    @Nested
    @DisplayName("학습목표를 생성할 때")
    class Create {

        @Test
        @DisplayName("유효한 값이면 학습목표가 생성된다")
        void success() {
            // given
            Long memberId = 5L;
            String title = "React 핵심 개념 복습";

            // when
            LearningGoal goal = LearningGoal.create(memberId, title);

            // then
            assertThat(goal.getMemberId()).isEqualTo(memberId);
            assertThat(goal.getTitle()).isEqualTo(title);
            assertThat(goal.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("memberId가 null이면 예외가 발생한다")
        void nullMemberId() {
            // when & then
            assertThatThrownBy(() -> LearningGoal.create(null, "제목"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("memberId");
        }

        @Test
        @DisplayName("title이 비어 있으면 예외가 발생한다")
        void blankTitle() {
            // when & then
            assertThatThrownBy(() -> LearningGoal.create(5L, " "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("title");
        }

        @Test
        @DisplayName("title이 30자를 초과하면 예외가 발생한다")
        void tooLongTitle() {
            // given
            String title = "가".repeat(31);

            // when & then
            assertThatThrownBy(() -> LearningGoal.create(5L, title))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("30자");
        }
    }

    @Nested
    @DisplayName("제목을 수정할 때")
    class UpdateTitle {

        @Test
        @DisplayName("새 제목으로 변경되고 updatedAt이 기록된다")
        void success() {
            // given
            LearningGoal goal = LearningGoal.create(5L, "기존 제목");

            // when
            goal.updateTitle("새 제목");

            // then
            assertThat(goal.getTitle()).isEqualTo("새 제목");
            assertThat(goal.getUpdatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("작성자 본인 여부를 확인할 때")
    class IsOwnedBy {

        @Test
        @DisplayName("작성자 본인이면 true를 반환한다")
        void owner() {
            // given
            LearningGoal goal = LearningGoal.create(5L, "제목");

            // when & then
            assertThat(goal.isOwnedBy(5L)).isTrue();
        }

        @Test
        @DisplayName("작성자가 아니면 false를 반환한다")
        void notOwner() {
            // given
            LearningGoal goal = LearningGoal.create(5L, "제목");

            // when & then
            assertThat(goal.isOwnedBy(6L)).isFalse();
        }
    }

    @Nested
    @DisplayName("만료 여부를 확인할 때")
    class IsExpired {

        @Test
        @DisplayName("생성 후 24시간이 지나면 만료된다")
        void expired() {
            // given
            LearningGoal goal = LearningGoal.create(5L, "제목");
            ReflectionTestUtils.setField(goal, "createdAt", LocalDateTime.now().minusHours(25));

            // when & then
            assertThat(goal.isExpired()).isTrue();
        }

        @Test
        @DisplayName("생성 후 24시간이 지나지 않으면 만료되지 않는다")
        void notExpired() {
            // given
            LearningGoal goal = LearningGoal.create(5L, "제목");
            ReflectionTestUtils.setField(goal, "createdAt", LocalDateTime.now().minusHours(1));

            // when & then
            assertThat(goal.isExpired()).isFalse();
        }
    }
}
