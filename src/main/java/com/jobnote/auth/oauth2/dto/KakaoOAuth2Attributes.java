package com.jobnote.auth.oauth2.dto;

import com.jobnote.domain.user.domain.SocialProvider;

import java.util.Map;

public class KakaoOAuth2Attributes extends OAuth2Attributes {

    public KakaoOAuth2Attributes(final Map<String, Object> attributes, final String userNameAttributeKey) {
        super((Map<String, Object>) attributes.get("kakao_account"), userNameAttributeKey);
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.KAKAO;
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }
}
