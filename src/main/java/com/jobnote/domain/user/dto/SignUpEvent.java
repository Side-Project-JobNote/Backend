package com.jobnote.domain.user.dto;

public record SignUpEvent(
        String email,
        String token
) {
}
