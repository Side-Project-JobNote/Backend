package com.jobnote.auth.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.auth.oauth2.dto.CustomOAuth2User;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenClaim;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.dto.UserTokenResponse;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

import static com.jobnote.global.common.Constants.CHARACTER_ENCODING;

@RequiredArgsConstructor
@Configuration
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User principal = (CustomOAuth2User) authentication.getPrincipal();
        final TokenClaim tokenClaim = TokenClaim.builder()
                .email(principal.getEmail())
                .role(principal.getAuthorities().iterator().next().getAuthority())
                .build();

        final Token token = tokenProvider.issueToken(tokenClaim);

        final ResponseCode responseCode = ResponseCode.OK;
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofSuccess(responseCode, UserTokenResponse.of(tokenClaim.userId(), token))));
    }
}
