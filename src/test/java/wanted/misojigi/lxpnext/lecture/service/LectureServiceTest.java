package wanted.misojigi.lxpnext.lecture.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import wanted.misojigi.lxpnext.lecture.domain.Lecture;
import wanted.misojigi.lxpnext.lecture.domain.LectureStatus;
import wanted.misojigi.lxpnext.lecture.dto.LectureCreateRequest;
import wanted.misojigi.lxpnext.lecture.dto.LectureCreateResponse;
import wanted.misojigi.lxpnext.lecture.repository.ContentRepository;
import wanted.misojigi.lxpnext.lecture.repository.LectureRepository;
import wanted.misojigi.lxpnext.member.repository.MemberRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("LectureService")
class LectureServiceTest {

    @Mock private LectureRepository lectureRepository;
    @Mock private ContentRepository contentRepository;
    @Mock private MemberRepository memberRepository;

    @InjectMocks private LectureService lectureService;

    private static final Long INSTRUCTOR_ID = 1L;

    @Nested
    @DisplayName("강의를 생성할 때")
    class CreateLecture {

        @Test
        @DisplayName("정상 요청이면 status가 PUBLIC으로 저장되고 응답 DTO가 반환된다")
        void success() {
            // given
            Lecture saved = Lecture.create(INSTRUCTOR_ID, "Spring Boot 기초", "Spring Boot 입문 강의입니다.");
            ReflectionTestUtils.setField(saved, "id", 1L);
            given(lectureRepository.save(any(Lecture.class))).willReturn(saved);

            LectureCreateRequest request = new LectureCreateRequest(
                    INSTRUCTOR_ID, "Spring Boot 기초", "Spring Boot 입문 강의입니다.");

            // when
            LectureCreateResponse response = lectureService.createLecture(request);

            // then
            assertThat(response.lectureId()).isEqualTo(1L);
            assertThat(response.instructorId()).isEqualTo(INSTRUCTOR_ID);
            assertThat(response.title()).isEqualTo("Spring Boot 기초");
            assertThat(response.description()).isEqualTo("Spring Boot 입문 강의입니다.");
            assertThat(response.status()).isEqualTo(LectureStatus.PUBLIC);
        }

        @Test
        @DisplayName("description이 없어도 강의가 생성된다")
        void successWithoutDescription() {
            // given
            Lecture saved = Lecture.create(INSTRUCTOR_ID, "Spring Boot 기초", null);
            ReflectionTestUtils.setField(saved, "id", 2L);
            given(lectureRepository.save(any(Lecture.class))).willReturn(saved);

            LectureCreateRequest request = new LectureCreateRequest(INSTRUCTOR_ID, "Spring Boot 기초", null);

            // when
            LectureCreateResponse response = lectureService.createLecture(request);

            // then
            assertThat(response.description()).isNull();
            assertThat(response.status()).isEqualTo(LectureStatus.PUBLIC);
        }
    }
}
