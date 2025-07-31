package com.jobnote.auth.token;

import lombok.Builder;

@Builder
public record TokenClaim(
        String email
) {
}
