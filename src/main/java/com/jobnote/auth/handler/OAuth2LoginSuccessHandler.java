package com.jobnote.auth.handler;

import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.domain.UserRole;
import com.jobnote.domain.user.service.AuthTokenService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AuthTokenService authTokenService;

    @Value("${app.frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        final Token token = authTokenService.saveAndGetToken(principal.getUserId());
        tokenProvider.addTokenToCookie(response, token);

        if (UserRole.GUEST.getKey().equals(principal.getRole())) {
            response.sendRedirect(frontendBaseUrl + signUpRequiredQueryParam(true));
            return;
        }

        response.sendRedirect(frontendBaseUrl + signUpRequiredQueryParam(false));
    }

    private String signUpRequiredQueryParam(final boolean result) {
        return String.format("?sign-up-required=%s", result);
    }
}
