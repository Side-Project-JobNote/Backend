package com.jobnote.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SocialProvider {

    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao"),
    ;

    private final String registrationId;
}
