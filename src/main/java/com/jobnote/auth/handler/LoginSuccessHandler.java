package com.jobnote.auth.handler;

import com.jobnote.auth.dto.CustomOAuth2User;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.service.AuthTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.jobnote.global.common.Constants.URI_USER_LOGIN;
import static com.jobnote.global.common.Constants.URI_USER_OAUTH2;

@RequiredArgsConstructor
@Component
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AuthTokenService authTokenService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (URI_USER_LOGIN.startsWith(request.getRequestURI())) {
            final CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            responseToken(response, principal.getUserId());
        }
        if (URI_USER_OAUTH2.startsWith(request.getRequestURI())) {
            final CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
            responseToken(response, principal.getUserId());
        }
    }

    private void responseToken(final HttpServletResponse response, final Long userId) throws IOException {
        final Token token = authTokenService.saveAndGetToken(userId);
        tokenProvider.responseToken(response, token);
    }
}
