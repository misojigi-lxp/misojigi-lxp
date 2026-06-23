package wanted.misojigi.lxpnext.review.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequest(

	@NotBlank(message = "후기 내용을 입력해주세요.")
	@Size(min = 1, max = 49, message = "후기 내용은 1자 이상 50자 미만이어야 합니다.")
	String content,

	@NotNull(message = "별점을 입력해주세요.")
	@Min(value = 1, message = "별점은 1점 이상이어야 합니다.")
	@Max(value = 5, message = "별점은 5점 이하이어야 합니다.")
	Integer rating
) {
}