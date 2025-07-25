package com.jobnote.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jobnote.common.Constants.*;

@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final JwtGenerator jwtGenerator;

    public Token issueToken(final long userId, final String role) {
        return Token.builder()
                .accessToken(jwtGenerator.generateAccessToken(userId, role))
                .refreshToken(jwtGenerator.generateRefreshToken())
                .build();
    }

    public Optional<Long> getUserIdFromPayload(final String token) {
        return Optional.of(jwtGenerator.getTokenPayload(token).get(CLAIM_NAME_USER_ID, Long.class));
    }

    public Optional<String> getRoleFromPayload(final String token) {
        return Optional.of(jwtGenerator.getTokenPayload(token).get(CLAIM_NAME_ROLE, String.class));
    }

    public Optional<String> getTokenTypeFromPayload(final String token) {
        return Optional.of(jwtGenerator.getTokenPayload(token).get(CLAIM_NAME_TOKEN_TYPE, String.class));
    }
}
