package com.jobnote.domain.user.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationTokenTest {

    @Test
    @DisplayName("토큰이 만료되지 않는다")
    void before_TokenExpired() {
        // given
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
        final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
        final LocalDateTime currentDate = expiryDate.minusMinutes(1);
        final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);

        // when
        boolean result = verificationToken.validateExpiration(currentDate);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰이 만료된다")
    void after_TokenExpired() {
        // given
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
        final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
        final LocalDateTime currentDate = expiryDate.plusMinutes(1);
        final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);

        // when
        boolean result = verificationToken.validateExpiration(currentDate);

        // then
        assertThat(result).isTrue();
    }
}