package com.jobnote.domain.user.event;

public record EmailVerificationEvent(
        String toEmail,
        String token
) {
}
