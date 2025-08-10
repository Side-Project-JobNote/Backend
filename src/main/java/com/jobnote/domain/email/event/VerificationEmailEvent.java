package com.jobnote.domain.email.event;

import com.jobnote.domain.email.domain.VerificationEmailType;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record VerificationEmailEvent(
        String toEmail,
        String token,
        VerificationEmailType type
) {
    public static VerificationEmailEvent of(final String toEmail, final String token, VerificationEmailType type) {
        return VerificationEmailEvent.builder()
                .toEmail(toEmail)
                .token(token)
                .type(type)
                .build();
    }
}
