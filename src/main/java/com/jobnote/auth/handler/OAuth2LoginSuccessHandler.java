package com.jobnote.auth.handler;

import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.domain.UserRole;
import com.jobnote.domain.user.service.AuthTokenService;
import com.jobnote.global.config.properties.FrontendProperties;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AuthTokenService authTokenService;
    private final FrontendProperties frontendProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        if (UserRole.GUEST.getKey().equals(principal.getRole())) {
            response.sendRedirect(guestRedirectUrl(principal.getEmail()));
            return;
        }

        final Token token = authTokenService.saveAndGetToken(principal.getUserId());
        tokenProvider.responseToken(response, token);

        response.sendRedirect(memberRedirectUrl());
    }

    private String guestRedirectUrl(final String email) {
        return UriComponentsBuilder.fromUriString(frontendProperties.baseUrl())
                .path(frontendProperties.mainPage())
                .queryParam("sign-up-required", true)
                .queryParam("email", email)
                .build()
                .toUriString();
    }

    private String memberRedirectUrl() {
        return frontendProperties.baseUrl() + frontendProperties.mainPage();
    }
}
