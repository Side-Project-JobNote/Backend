package com.jobnote.domain.user.dto;

import com.jobnote.domain.user.domain.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserSignUpRequest(

        @Email(message = "형식에 맞는 이메일을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
        String password,

        @NotBlank(message = "닉네임은 비어있을 수 없습니다.")
        String nickname
) {

        public User toEntity(final String encodedPassword) {
                return User.signUp(email, encodedPassword, nickname);
        }
}
