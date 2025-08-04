package com.jobnote.auth.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.domain.UserRole;
import com.jobnote.domain.user.service.AuthTokenService;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.util.ResponseUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AuthTokenService authTokenService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();

        if (UserRole.GUEST.getKey().equals(principal.getRole())) {
            responseTokenAndError(response, principal.getUserId());
            return;
        }

        responseToken(response, principal.getUserId());
    }

    private void responseToken(final HttpServletResponse response, final Long userId) throws IOException {
        final Token token = authTokenService.saveAndGetToken(userId);
        tokenProvider.responseToken(response, token);
    }

    private void responseTokenAndError(final HttpServletResponse response, final Long userId) throws IOException {
        final Token token = authTokenService.saveAndGetToken(userId);
        tokenProvider.addTokenToCookie(response, token);
        ResponseUtil.responseError(response, objectMapper, ResponseCode.NOT_YET_SIGNED_UP);
    }
}
