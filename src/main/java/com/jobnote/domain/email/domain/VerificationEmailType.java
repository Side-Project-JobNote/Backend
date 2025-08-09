package com.jobnote.domain.email.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VerificationEmailType {

    SIGN_UP("회원가입"),
    RESET_PASSWORD("비밀번호 재설정"),
    ;

    private final String description;
}
