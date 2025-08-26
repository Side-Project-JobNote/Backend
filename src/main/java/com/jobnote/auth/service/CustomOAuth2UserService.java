package com.jobnote.auth.service;

import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.auth.dto.OAuth2Attributes;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(final OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User oAuth2User = super.loadUser(userRequest);
        final String registrationId = userRequest.getClientRegistration().getRegistrationId();
        final String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        final Map<String, Object> attributes = oAuth2User.getAttributes();

        final OAuth2Attributes oAuth2Attributes = OAuth2Attributes.of(registrationId, attributes, userNameAttributeName);
        final User user = getUser(oAuth2Attributes);

        return new CustomUserDetails(user, oAuth2Attributes);
    }

    private User getUser(final OAuth2Attributes oAuth2Attributes) {
        return userRepository.findBySocialProviderAndSocialId(oAuth2Attributes.getProvider(), oAuth2Attributes.getProviderId())
                .orElseGet(() -> userRepository.save(
                        User.socialSignUp(UUID.randomUUID().toString(), oAuth2Attributes.getEmail(), oAuth2Attributes.getProvider(), oAuth2Attributes.getProviderId()))
                );
    }
}
