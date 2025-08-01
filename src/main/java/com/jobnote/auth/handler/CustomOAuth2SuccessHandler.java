package com.jobnote.auth.handler;

import com.jobnote.auth.dto.CustomOAuth2User;
import com.jobnote.auth.token.TokenClaim;
import com.jobnote.auth.token.TokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@RequiredArgsConstructor
@Configuration
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        final CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        final TokenClaim tokenClaim = TokenClaim.builder()
                .email(principal.getEmail())
                .build();
        tokenProvider.responseToken(response, tokenProvider.issueToken(tokenClaim));
    }
}
