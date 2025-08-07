package com.jobnote.domain.verificationtoken.domain;

import com.jobnote.domain.user.domain.User;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.jobnote.global.common.ResponseCode.ALREADY_VERIFIED_TOKEN;
import static com.jobnote.global.common.ResponseCode.EXPIRED_VERIFICATION_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificationTokenTest {

    @Test
    @DisplayName("토큰 생성 시 상태는 PENDING이다.")
    void created_StatusIsPending() {
        // given
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
        final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);

        // when
        final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);

        // then
        assertThat(verificationToken.getStatus()).isEqualTo(VerificationTokenStatus.PENDING);
    }

    @Nested
    @DisplayName("토큰 만료 검사")
    class ValidateTokenExpired {
        @Test
        @DisplayName("토큰이 만료되지 않았으면 아무일도 일어나지 않는다.")
        void notExpired() {
            // given
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final LocalDateTime currentDate = expiryDate.minusMinutes(1);
            final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);

            // when
            verificationToken.validateExpired(currentDate);

            // then
            assertThat(verificationToken.getStatus()).isEqualTo(VerificationTokenStatus.PENDING);
        }

        @Test
        @DisplayName("토큰이 만료되었으면 예외를 발생하고 상태는 EXPIRED로 변경된다.")
        void expired_ThrowsException_ChangeStatus() {
            // given
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final LocalDateTime currentDate = expiryDate.plusMinutes(1);
            final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);

            // when & then
            assertThatThrownBy(() -> verificationToken.validateExpired(currentDate))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(EXPIRED_VERIFICATION_TOKEN.getMessage());
            assertThat(verificationToken.getStatus()).isEqualTo(VerificationTokenStatus.EXPIRED);
        }
    }

    @Nested
    @DisplayName("토큰 기검증 검사")
    class ValidateTokenVerified {
        @Test
        @DisplayName("토큰이 아직 검증되지 않았으면 아무일도 일어나지 않는다.")
        void notVerified() {
            // given
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);

            // when
            verificationToken.validateVerified();

            // then
            assertThat(verificationToken.getStatus()).isEqualTo(VerificationTokenStatus.PENDING);
        }

        @Test
        @DisplayName("토큰이 이미 검증되었으면 예외를 발생한다.")
        void verified_ThrowsException() {
            // given
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
            final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);
            verificationToken.complete();

            // when & then
            assertThatThrownBy(verificationToken::validateVerified)
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(ALREADY_VERIFIED_TOKEN.getMessage());
            assertThat(verificationToken.getStatus()).isEqualTo(VerificationTokenStatus.VERIFIED);
        }
    }

    @Test
    @DisplayName("토큰 검증을 완료하면 상태가 VERIFIED로 변경된다.")
    void completeToken() {
        // given
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
        final LocalDateTime expiryDate = LocalDateTime.of(2025, 7, 30, 12, 0);
        final VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), user, expiryDate);

        // when
        verificationToken.complete();

        // then
        assertThat(verificationToken.getStatus()).isEqualTo(VerificationTokenStatus.VERIFIED);
    }
}