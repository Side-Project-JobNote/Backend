package com.jobnote.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLoginRequest(

        @Email(message = "형식에 맞는 이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
        String password
) {
}
