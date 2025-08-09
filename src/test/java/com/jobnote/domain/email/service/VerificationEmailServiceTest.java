package com.jobnote.domain.email.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.email.domain.VerificationEmail;
import com.jobnote.domain.email.domain.VerificationEmailStatus;
import com.jobnote.domain.email.repository.VerificationEmailRepository;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_VERIFICATION_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class VerificationEmailServiceTest extends ServiceUnitTest {

    @Mock
    private VerificationEmailRepository verificationEmailRepository;

    @InjectMocks
    private VerificationEmailService verificationEmailService;

    @Nested
    @DisplayName("token 필드로 VerificationToken 엔티티 조회")
    class GetVerificationTokenByEmail {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final User user = mock(User.class);
            final String token = "testToken";
            final LocalDateTime emailVerificationExpiryDate = LocalDateTime.of(2025, 8, 6, 11, 31);
            final VerificationEmail verificationEmail = VerificationEmail.create(token, user, emailVerificationExpiryDate);

            given(verificationEmailRepository.findByToken(token)).willReturn(Optional.of(verificationEmail));

            // when
            final VerificationEmail result = verificationEmailService.getVerificationEmailByToken(token);

            // then
            assertThat(result).isEqualTo(verificationEmail);
        }

        @Test
        @DisplayName("실패 - 엔티티가 존재하지 않는다.")
        void fail_NotFoundEntity() {
            // given
            final String token = "testToken";
            given(verificationEmailRepository.findByToken(token)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> verificationEmailService.getVerificationEmailByToken(token))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_VERIFICATION_EMAIL.getMessage());
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class VerifyToken {
        @Test
        @DisplayName("성공 - 검증 토큰의 상태는 VERIFIED가 된다.")
        void success() {
            // given
            final User user = mock(User.class);
            final String token = "testToken";
            final LocalDateTime currentDate = LocalDateTime.of(2025, 7, 29, 12, 0);
            final LocalDateTime emailVerificationExpiryDate = LocalDateTime.of(2025, 8, 6, 11, 31);
            final VerificationEmail verificationEmail = VerificationEmail.create(token, user, emailVerificationExpiryDate);

            given(verificationEmailRepository.findByToken(token)).willReturn(Optional.of(verificationEmail));

            // when
            final VerificationEmail result = verificationEmailService.verifyToken(token, currentDate);

            // then
            assertThat(result).isEqualTo(verificationEmail);
            assertThat(result.getStatus()).isEqualTo(VerificationEmailStatus.VERIFIED);
        }
    }
}