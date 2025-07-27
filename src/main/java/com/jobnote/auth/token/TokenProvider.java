package com.jobnote.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jobnote.global.common.Constants.*;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    private final JwtProvider jwtProvider;

    public Token issueToken(final long userId, final String role) {
        return Token.builder()
                .accessToken(jwtProvider.generateAccessToken(userId, role))
                .refreshToken(jwtProvider.generateRefreshToken())
                .build();
    }

    public Optional<Long> getUserIdFromPayload(final String token) {
        return Optional.of(jwtProvider.getTokenPayload(token).get(CLAIM_NAME_USER_ID, Long.class));
    }

    public Optional<String> getRoleFromPayload(final String token) {
        return Optional.of(jwtProvider.getTokenPayload(token).get(CLAIM_NAME_ROLE, String.class));
    }
}
