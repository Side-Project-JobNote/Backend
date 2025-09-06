package com.jobnote.auth.handler;

import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.user.domain.UserRole;
import com.jobnote.domain.code.service.TempCodeService;
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

    private final TempCodeService tempCodeService;
    private final FrontendProperties frontendProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();

        if (UserRole.GUEST.getKey().equals(principal.getRole())) {
            response.sendRedirect(guestRedirectUrl(principal.getEmail()));
            return;
        }

        final String code = tempCodeService.create(principal.getUserId());
        response.sendRedirect(memberRedirectUrl(code));
    }

    private String guestRedirectUrl(final String email) {
        return UriComponentsBuilder.fromUriString(frontendProperties.baseUrl())
                .path(frontendProperties.mainPage())
                .queryParam("sign-up-required", true)
                .queryParam("email", email)
                .build()
                .toUriString();
    }

    private String memberRedirectUrl(final String code) {
        return UriComponentsBuilder.fromUriString(frontendProperties.baseUrl())
                .path(frontendProperties.mainPage())
                .queryParam("code", code)
                .build()
                .toUriString();
    }
}
