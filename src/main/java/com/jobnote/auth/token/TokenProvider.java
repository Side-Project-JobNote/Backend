package com.jobnote.auth.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.util.CookieUtil.createResponseCookie;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    public Token issueToken(final TokenClaim tokenClaim) {
        return Token.builder()
                .accessToken(jwtProvider.generateAccessToken(tokenClaim))
                .refreshToken(jwtProvider.generateRefreshToken())
                .build();
    }

    public Optional<String> getEmailFromPayload(final String token) {
        return Optional.of(jwtProvider.validateAndGetTokenPayload(token, CLAIM_VALUE_ACCESS_TOKEN).get(CLAIM_NAME_EMAIL, String.class));
    }

    public void validateRefreshToken(final String refreshToken) {
        jwtProvider.validateAndGetTokenPayload(refreshToken, CLAIM_VALUE_REFRESH_TOKEN);
    }

    public LocalDateTime getExpiration(final String token, final String tokenType) {
        return jwtProvider.validateAndGetTokenPayload(token, tokenType).getExpiration()
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public void responseToken(final HttpServletResponse response, final Token token) throws IOException {
        final ResponseCode responseCode = ResponseCode.OK;
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        ResponseCookie accessTokenCookie = createResponseCookie(COOKIE_NAME_ACCESS_TOKEN, token.accessToken(), COOKIE_PATH_ACCESS_TOKEN, Duration.ofMillis(jwtProvider.getJwtProperties().accessToken().expirationTime()));
        ResponseCookie refreshTokenCookie = createResponseCookie(COOKIE_NAME_REFRESH_TOKEN, token.refreshToken(), COOKIE_PATH_REFRESH_TOKEN, Duration.ofMillis(jwtProvider.getJwtProperties().refreshToken().expirationTime()));

        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofSuccess(responseCode)));
    }
}
