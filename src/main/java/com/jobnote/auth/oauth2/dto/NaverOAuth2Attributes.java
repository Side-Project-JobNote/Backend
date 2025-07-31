package com.jobnote.auth.oauth2.dto;

import com.jobnote.domain.user.domain.SocialProvider;

import java.util.Map;

public class NaverOAuth2Attributes extends OAuth2Attributes {

    public NaverOAuth2Attributes(final Map<String, Object> attributes, final String userNameAttributeKey) {
        super((Map<String, Object>) attributes.get("response"), userNameAttributeKey);
    }

    @Override
    public SocialProvider getProvider() {
        return SocialProvider.NAVER;
    }

    @Override
    public String getEmail() {
        return (String) getAttributes().get("email");
    }
}
