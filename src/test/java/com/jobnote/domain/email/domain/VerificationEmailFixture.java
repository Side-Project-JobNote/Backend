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

    public static VerificationEmail createPendingSignUp(final String token, final User user, final LocalDateTime expiryDate) {
        return VerificationEmail.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .status(VerificationEmailStatus.PENDING)
                .type(VerificationEmailType.SIGN_UP)
                .build();
    }

    public static VerificationEmail createExpiredSignUp(final String token, final User user, final LocalDateTime expiryDate) {
        return VerificationEmail.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .status(VerificationEmailStatus.EXPIRED)
                .type(VerificationEmailType.SIGN_UP)
                .build();
    }
}
