package com.jobnote.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRequest(

        @Email(message = "형식에 맞는 이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
        String password,

        @NotBlank(message = "이름은 비어있을 수 없습니다.")
        String name,

        String avatarUrl
) {
}
