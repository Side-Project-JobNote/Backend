package com.jobnote.domain.user.event;

public record SignUpEvent(
        String toEmail,
        String verificationToken
) {
}
