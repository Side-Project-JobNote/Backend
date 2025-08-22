package com.jobnote.domain.email.domain;

import com.jobnote.domain.user.domain.User;

import java.time.LocalDateTime;

public class VerificationEmailFixture {

    public static VerificationEmail createVerifiedResetPassword(final String token, final User user, final LocalDateTime expiryDate) {
        return VerificationEmail.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .status(VerificationEmailStatus.VERIFIED)
                .type(VerificationEmailType.RESET_PASSWORD)
                .build();
    }

    public static VerificationEmail createVerifiedSignUp(final String token, final User user, final LocalDateTime expiryDate) {
        return VerificationEmail.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .status(VerificationEmailStatus.VERIFIED)
                .type(VerificationEmailType.SIGN_UP)
                .build();
    }
}
