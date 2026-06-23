package wanted.misojigi.lxpnext.goal.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("DetailGoal 도메인")
class DetailGoalTest {

    @Nested
    @DisplayName("세부목표를 생성할 때")
    class Create {

        @Test
        @DisplayName("유효한 값이면 세부목표가 생성되고 미완료 상태다")
        void success() {
            // when
            DetailGoal detailGoal = DetailGoal.create(1L, "useState 다시 보기", 1);

            // then
            assertThat(detailGoal.getLearningGoalId()).isEqualTo(1L);
            assertThat(detailGoal.getContent()).isEqualTo("useState 다시 보기");
            assertThat(detailGoal.getSortOrder()).isEqualTo(1);
            assertThat(detailGoal.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("learningGoalId가 null이면 예외가 발생한다")
        void nullLearningGoalId() {
            // when & then
            assertThatThrownBy(() -> DetailGoal.create(null, "내용", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("learningGoalId");
        }

        @Test
        @DisplayName("content가 비어 있으면 예외가 발생한다")
        void blankContent() {
            // when & then
            assertThatThrownBy(() -> DetailGoal.create(1L, " ", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("content");
        }

        @Test
        @DisplayName("content가 50자를 초과하면 예외가 발생한다")
        void tooLongContent() {
            // given
            String content = "가".repeat(51);

            // when & then
            assertThatThrownBy(() -> DetailGoal.create(1L, content, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("50자");
        }

        @Test
        @DisplayName("sortOrder가 1 미만이면 예외가 발생한다")
        void invalidSortOrder() {
            // when & then
            assertThatThrownBy(() -> DetailGoal.create(1L, "내용", 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("sortOrder");
        }
    }

    @Nested
    @DisplayName("세부목표를 완료 처리할 때")
    class Complete {

        @Test
        @DisplayName("completed가 true로 변경된다")
        void success() {
            // given
            DetailGoal detailGoal = DetailGoal.create(1L, "내용", 1);

            // when
            detailGoal.complete();

            // then
            assertThat(detailGoal.isCompleted()).isTrue();
        }
    }
}
