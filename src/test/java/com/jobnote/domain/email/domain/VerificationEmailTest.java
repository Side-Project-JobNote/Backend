package com.jobnote.domain.email.domain;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserFixture;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.jobnote.global.common.ResponseCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationEmailTest {

    @Test
    @DisplayName("토큰 생성 시 상태는 PENDING이다.")
    void created_StatusIsPending() {
        // given
        final User user = UserFixture.createGuest(1L, "testEmail@test.com", "testPassword", "testNickname");
        final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);

        // when
        final VerificationEmail verificationEmail = VerificationEmail.create(UUID.randomUUID().toString(), user, expiryDate, VerificationEmailType.SIGN_UP);

        // then
        assertThat(verificationEmail.getStatus()).isEqualTo(VerificationEmailStatus.PENDING);
    }

    @Nested
    @DisplayName("토큰 만료 검사")
    class ValidateTokenExpired {
        @Test
        @DisplayName("토큰이 만료되지 않았으면 아무일도 일어나지 않는다.")
        void notExpired() {
            // given
            final User user = UserFixture.createGuest(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final LocalDateTime currentDate = expiryDate.minusMinutes(1);
            final VerificationEmail verificationEmail = VerificationEmailFixture.createPendingSignUp(UUID.randomUUID().toString(), user, expiryDate);

            // when
            verificationEmail.validateExpired(currentDate);

            // then
            assertThat(verificationEmail.getStatus()).isEqualTo(VerificationEmailStatus.PENDING);
        }

        @Test
        @DisplayName("토큰이 만료되었으면 예외를 발생하고 상태는 EXPIRED로 변경된다.")
        void expired_ThrowsException_ChangeStatus() {
            // given
            final User user = UserFixture.createGuest(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final LocalDateTime currentDate = expiryDate.plusMinutes(1);
            final VerificationEmail verificationEmail = VerificationEmailFixture.createExpiredSignUp(UUID.randomUUID().toString(), user, expiryDate);

            // when & then
            assertThatThrownBy(() -> verificationEmail.validateExpired(currentDate))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(EXPIRED_VERIFICATION_EMAIL.getMessage());
            assertThat(verificationEmail.getStatus()).isEqualTo(VerificationEmailStatus.EXPIRED);
        }
    }

    @Nested
    @DisplayName("토큰 검증 완료 여부 검사")
    class ValidateTokenVerified {
        @Test
        @DisplayName("토큰이 검증되었으면 아무일도 일어나지 않는다.")
        void verified() {
            // given
            final User user = UserFixture.createGuest(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final VerificationEmail verificationEmail = VerificationEmailFixture.createVerifiedSignUp(UUID.randomUUID().toString(), user, expiryDate);

            // when
            verificationEmail.validateVerified();

            // then
            assertThat(verificationEmail.getStatus()).isEqualTo(VerificationEmailStatus.VERIFIED);
        }

        @Test
        @DisplayName("토큰이 검증되지 않았으면 예외를 발생한다.")
        void notVerified_ThrowsException() {
            // given
            final User user = UserFixture.createGuest(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final VerificationEmail verificationEmail = VerificationEmailFixture.createPendingSignUp(UUID.randomUUID().toString(), user, expiryDate);

            // when & then
            assertThatThrownBy(verificationEmail::validateVerified)
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(VERIFICATION_EMAIL_NOT_YET_VERIFIED.getMessage());
            assertThat(verificationEmail.getStatus()).isEqualTo(VerificationEmailStatus.PENDING);
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class VerifyToken {
        @Test
        @DisplayName("성공 - 토큰의 상태가 VERIFIED로 변경된다.")
        void success() {
            // given
            final User user = UserFixture.createGuest(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final VerificationEmail verificationEmail = VerificationEmailFixture.createPendingSignUp(UUID.randomUUID().toString(), user, expiryDate);

            // when
            verificationEmail.verify();

            // then
            assertThat(verificationEmail.getStatus()).isEqualTo(VerificationEmailStatus.VERIFIED);
        }

        @Test
        @DisplayName("토큰이 이미 검증되었으면 예외를 발생한다.")
        void fail_AlreadyVerified_ThrowsException() {
            // given
            final User user = UserFixture.createMember(1L, "testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final VerificationEmail verificationEmail = VerificationEmailFixture.createVerifiedSignUp(UUID.randomUUID().toString(), user, expiryDate);

            // when & then
            assertThatThrownBy(verificationEmail::verify)
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(VERIFICATION_EMAIL_ALREADY_VERIFIED.getMessage());
            assertThat(verificationEmail.getStatus()).isEqualTo(VerificationEmailStatus.VERIFIED);
        }
    }
}