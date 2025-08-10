package com.jobnote.domain.email.dto;

import com.jobnote.domain.email.domain.VerificationEmailType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record VerificationEmailRequest(

        @Email(message = "형식에 맞는 이메일을 입력해주세요.")
        String email,

        @NotNull
        VerificationEmailType type
) {
}
