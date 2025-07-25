package com.jobnote.auth.jwt;

import lombok.Builder;

@Builder
public record Token(
        String accessToken,
        String refreshToken
) {
}
