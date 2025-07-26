package com.jobnote.auth.token;

import lombok.Builder;

@Builder
public record Token(
        String accessToken,
        String refreshToken
) {
}
