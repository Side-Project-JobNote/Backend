package com.jobnote.domain.user.dto;

import jakarta.validation.constraints.Email;

public record UserResetPasswordEmailRequest(

        @Email(message = "형식에 맞는 이메일을 입력해주세요.")
        String email
) {
}
