package wanted.misojigi.lxpnext.lecture.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LectureCreateRequest(
	@NotNull(message = "강사 ID는 필수값입니다.")
	Long instructorId,

	@NotBlank(message = "제목은 필수값입니다.")
	String title,

	String description
) {
}
