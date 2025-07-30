package com.jobnote.auth.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.jobnote.global.common.Constants.*;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    private final JwtProvider jwtProvider;

    public Token issueToken(final TokenClaim tokenClaim) {
        return Token.builder()
                .accessToken(jwtProvider.generateAccessToken(tokenClaim))
                .refreshToken(jwtProvider.generateRefreshToken())
                .build();
    }

    public Optional<String> getEmailFromPayload(final String token) {
        return Optional.of(jwtProvider.getTokenPayload(token).get(CLAIM_NAME_EMAIL, String.class));
    }
}
