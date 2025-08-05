package com.jobnote.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record SocialSignUpRequest(

        @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
        String nickname
) {
}
