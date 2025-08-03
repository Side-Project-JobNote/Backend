package com.jobnote.domain.user.repository;

import com.jobnote.JpaTest;
import com.jobnote.domain.user.domain.RefreshToken;
import com.jobnote.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class RefreshTokenRepositoryTest extends JpaTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Nested
    @DisplayName("토큰 존재 여부 검사")
    class FindByEmail {
        @Test
        @DisplayName("토큰이 존재하면 true를 반환한다")
        void findByToken_Exists_ReturnsTrue() {
            // given
            final String token = "testToken";
            final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");

            userRepository.save(user);
            refreshTokenRepository.save(RefreshToken.of(user, token, LocalDateTime.of(2025, 8, 3, 16, 47, 0)));

            // when
            boolean result = refreshTokenRepository.existsByToken(token);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("토큰이 존재하지 않으면 false를 반환한다")
        void findByToken_NotExists_ReturnsFalse() {
            // given
            final String token = "testToken";

            // when
            boolean result = refreshTokenRepository.existsByToken(token);

            // then
            assertThat(result).isFalse();
        }
    }

    @Test
    @DisplayName("토큰값으로 삭제한다")
    void deleteByToken() {
        // given
        final String token = "testToken";
        final User user = User.signUp("testEmail@test.com", "testPassword", "testNickname");

        userRepository.save(user);
        refreshTokenRepository.save(RefreshToken.of(user, token, LocalDateTime.of(2025, 8, 3, 16, 47, 0)));

        // when
        refreshTokenRepository.deleteByToken(token);

        // then
        assertThat(refreshTokenRepository.count()).isZero();
    }
}