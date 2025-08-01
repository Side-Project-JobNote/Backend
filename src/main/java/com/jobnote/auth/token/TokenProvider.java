package com.jobnote.auth.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.jobnote.global.common.Constants.*;
import static com.jobnote.global.util.ResponseUtil.createResponseCookie;

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

    public void responseToken(final HttpServletResponse response, final Token token) throws IOException {
        final ResponseCode responseCode = ResponseCode.OK;
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);

        response.addHeader(HttpHeaders.SET_COOKIE,
                createResponseCookie(COOKIE_NAME_ACCESS_TOKEN, token.accessToken(), COOKIE_PATH_ACCESS_TOKEN, Math.toIntExact(jwtProvider.getJwtProperties().accessToken().expirationTime())));

        response.addHeader(HttpHeaders.SET_COOKIE,
                createResponseCookie(COOKIE_NAME_REFRESH_TOKEN, token.refreshToken(), COOKIE_PATH_REFRESH_TOKEN, Math.toIntExact(jwtProvider.getJwtProperties().refreshToken().expirationTime())));

        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofSuccess(responseCode)));
    }
}
