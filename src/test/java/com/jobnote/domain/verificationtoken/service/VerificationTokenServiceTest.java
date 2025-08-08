package com.jobnote.domain.verificationtoken.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.verificationtoken.domain.VerificationToken;
import com.jobnote.domain.verificationtoken.repository.VerificationTokenRepository;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_VERIFICATION_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class VerificationTokenServiceTest extends ServiceUnitTest {

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @InjectMocks
    private VerificationTokenService verificationTokenService;

    @Nested
    @DisplayName("token 필드로 VerificationToken 엔티티 조회")
    class GetVerificationTokenByToken {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final User user = mock(User.class);
            final String token = "testToken";
            final LocalDateTime emailVerificationExpiryDate = LocalDateTime.of(2025, 8, 6, 11, 31);
            final VerificationToken verificationToken = VerificationToken.create(token, user, emailVerificationExpiryDate);

            given(verificationTokenRepository.findByToken(token)).willReturn(Optional.of(verificationToken));

            // when
            final VerificationToken result = verificationTokenService.getVerificationTokenByToken(token);

            // then
            assertThat(result).isEqualTo(verificationToken);
        }

        @Test
        @DisplayName("실패 - 엔티티가 존재하지 않는다.")
        void fail_NotFoundEntity() {
            // given
            final String token = "testToken";
            given(verificationTokenRepository.findByToken(token)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> verificationTokenService.getVerificationTokenByToken(token))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_VERIFICATION_TOKEN.getMessage());
        }
    }
}