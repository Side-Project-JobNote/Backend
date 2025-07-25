package com.jobnote.auth.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.jobnote.common.Constants.CLAIM_NAME_ROLE;
import static com.jobnote.common.Constants.CLAIM_NAME_USER_ID;

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

    public long getUserIdFromPayload(final String token) {
        return jwtGenerator.getTokenPayload(token).get(CLAIM_NAME_USER_ID, Long.class);
    }

    public String getRoleFromPayload(final String token) {
        return jwtGenerator.getTokenPayload(token).get(CLAIM_NAME_ROLE, String.class);
    }

}
