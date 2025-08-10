package com.jobnote.domain.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserResetPasswordRequest(

        @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
        String newPassword
) {
}
