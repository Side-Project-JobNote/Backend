package com.jobnote.domain.user.event;

import com.jobnote.domain.email.domain.VerificationEmailType;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record EmailVerificationEvent(
        String toEmail,
        String token,
        VerificationEmailType type
) {
    public static EmailVerificationEvent of(final String toEmail, final String token, VerificationEmailType type) {
        return EmailVerificationEvent.builder()
                .toEmail(toEmail)
                .token(token)
                .type(type)
                .build();
    }
}
