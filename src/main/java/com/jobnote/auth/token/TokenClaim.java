package com.jobnote.auth.token;

import lombok.Builder;

@Builder
public record TokenClaim(
        Long userId,
        String email,
        String role
) {
}
