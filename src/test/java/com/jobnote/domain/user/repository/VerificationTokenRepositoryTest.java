package com.jobnote.domain.user.repository;

import com.jobnote.JpaTest;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.VerificationToken;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationTokenRepositoryTest extends JpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Nested
    @DisplayName("토큰 조회")
    class FindByToken {
        @Test
        @DisplayName("토큰이 존재하면 엔티티를 반환한다")
        void findByToken_Exists_ReturnsEntity() {
            // given
            final String token = "testToken";
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");
            final LocalDateTime expiryDate = LocalDateTime.of(2025, 8, 3, 12, 31, 0);

            userRepository.save(user);
            verificationTokenRepository.save(VerificationToken.create(token, user, expiryDate));

            // when
            Optional<VerificationToken> result = verificationTokenRepository.findByToken(token);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.get().getToken()).isEqualTo(token);
            assertThat(result.get().getUser()).isEqualTo(user);
            assertThat(result.get().getExpiryDate()).isEqualTo(expiryDate);
        }

        @Test
        @DisplayName("토큰이 존재하지 않으면 Empty를 반환한다")
        void findByToken_NotExists_ReturnsEmpty() {
            // given
            final String token = "testToken";

            // when
            Optional<VerificationToken> result = verificationTokenRepository.findByToken(token);

            // then
            assertThat(result).isEmpty();
        }
    }
}