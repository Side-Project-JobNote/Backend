package com.jobnote.auth.dto;

import com.jobnote.domain.user.domain.SocialProvider;
import com.jobnote.global.exception.JobNoteException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import static com.jobnote.global.common.ResponseCode.NOT_SUPPORTED_SOCIAL_PROVIDER;

@Getter
@RequiredArgsConstructor
public abstract class OAuth2Attributes {

    private final Map<String, Object> attributes;

    private final String userNameAttributeKey;

    abstract public SocialProvider getProvider();

    abstract public String getEmail();

    public String getProviderId() {
        return String.valueOf(attributes.get(userNameAttributeKey));
    }

    public static OAuth2Attributes of(final String registrationId, final Map<String, Object> attributes, final String userNameAttributeKey) {
        if (SocialProvider.GOOGLE.getRegistrationId().equals(registrationId)) {
            return new GoogleOAuth2Attributes(attributes, userNameAttributeKey);
        }

        if (SocialProvider.NAVER.getRegistrationId().equals(registrationId)) {
            return new NaverOAuth2Attributes(attributes, "id");
        }

        if (SocialProvider.KAKAO.getRegistrationId().equals(registrationId)) {
            return new KakaoOAuth2Attributes(attributes, "id");
        }

        throw new JobNoteException(NOT_SUPPORTED_SOCIAL_PROVIDER);
    }
}
