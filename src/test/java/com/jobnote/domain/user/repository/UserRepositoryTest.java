package com.jobnote.domain.user.repository;

import com.jobnote.JpaTest;
import com.jobnote.domain.user.domain.SocialProvider;
import com.jobnote.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryTest extends JpaTest {

    @Autowired
    private UserRepository userRepository;

    @Nested
    @DisplayName("회원가입")
    class SaveEmailSignUp {
        @Test
        @DisplayName("이메일 회원가입 시 avatarUrl, socialEmail, socialProvider, socialId 필드만 null 이다")
        void saveEmailSignUp() {
            // given
            final String email = "testEmail@email.com";
            final String password = "testPassword";
            final String nickname = "testNickname";

            // when
            User result = userRepository.save(User.signUp(email, password, nickname));

            // then
            assertThat(result.getAvatarUrl()).isNull();
            assertThat(result.getSocialEmail()).isNull();
            assertThat(result.getSocialProvider()).isNull();
            assertThat(result.getSocialId()).isNull();

            assertThat(result.getId()).isNotNull();
            assertThat(result.getEmail()).isNotNull();
            assertThat(result.getPassword()).isNotNull();
            assertThat(result.getNickname()).isNotNull();
            assertThat(result.getRole()).isNotNull();
        }

        @Test
        @DisplayName("소셜 로그인 회원가입 시 password, nickname, avatarUrl 필드만 null 이다")
        void findByEmail_NotExists_ReturnsEmpty() {
            // given
            final String email = "testEmail@test.com";
            final String socialEmail = "testSocialEmail@test.com";
            final SocialProvider socialProvider = SocialProvider.GOOGLE;
            final String socialId = "testSocialId";

            // when
            User result = userRepository.save(User.socialSignUp(email, socialEmail, socialProvider, socialId));

            // then
            assertThat(result.getPassword()).isNull();
            assertThat(result.getNickname()).isNull();
            assertThat(result.getAvatarUrl()).isNull();

            assertThat(result.getId()).isNotNull();
            assertThat(result.getEmail()).isNotNull();
            assertThat(result.getRole()).isNotNull();
            assertThat(result.getSocialEmail()).isNotNull();
            assertThat(result.getSocialProvider()).isNotNull();
            assertThat(result.getSocialId()).isNotNull();
        }
    }

    @Nested
    @DisplayName("이메일 조회")
    class FindByEmail {
        @Test
        @DisplayName("이메일이 존재하면 엔티티를 반환한다")
        void findByEmail_Exists_ReturnsEntity() {
            // given
            final String email = "testEmail@email.com";
            final String password = "testPassword";
            final String nickname = "testNickname";
            userRepository.save(User.signUp(email, password, nickname));

            // when
            Optional<User> result = userRepository.findByEmail(email);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.get().getEmail()).isEqualTo(email);
            assertThat(result.get().getPassword()).isNotBlank();
            assertThat(result.get().getNickname()).isEqualTo(nickname);
        }

        @Test
        @DisplayName("이메일이 존재하지 않으면 Empty를 반환한다")
        void findByEmail_NotExists_ReturnsEmpty() {
            // given
            final String email = "testEmail@email.com";

            // when
            Optional<User> result = userRepository.findByEmail(email);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("닉네임 존재 여부 검사")
    class ExistsByNickname {
        @Test
        @DisplayName("닉네임이 존재하면 true를 반환한다")
        void existsByNickname_Exists_ReturnsTrue() {
            // given
            final String email = "testEmail@email.com";
            final String password = "testPassword";
            final String nickname = "testNickname";
            userRepository.save(User.signUp(email, password, nickname));

            // when
            boolean result = userRepository.existsByNickname(nickname);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("닉네임이 존재하지 않으면 false를 반환한다")
        void existsByNickname_NotExists_ReturnsFalse() {
            // given
            final String nickname = "testNickname";

            // when
            boolean result = userRepository.existsByNickname(nickname);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("이메일 존재 여부 검사")
    class ExistsByEmail {
        @Test
        @DisplayName("이메일이 존재하면 true를 반환한다")
        void existsByEmail_Exists_ReturnsTrue() {
            // given
            final String email = "testEmail@email.com";
            final String password = "testPassword";
            final String nickname = "testNickname";
            userRepository.save(User.signUp(email, password, nickname));

            // when
            boolean result = userRepository.existsByEmail(email);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("이메일이 존재하지 않으면 false를 반환한다")
        void existsByEmail_NotExists_ReturnsFalse() {
            // given
            final String email = "testEmail@email.com";

            // when
            boolean result = userRepository.existsByNickname(email);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("소셜 로그인 조회")
    class FindBySocialProviderAndSocialId {
        @Test
        @DisplayName("소셜 로그인 회원이 존재하면 엔티티를 반환한다")
        void findBySocialProviderAndSocialId_Exists_ReturnsEntity() {
            // given
            final String email = "testEmail@test.com";
            final String socialEmail = "testSocialEmail@test.com";
            final SocialProvider socialProvider = SocialProvider.GOOGLE;
            final String socialId = "testSocialId";
            userRepository.save(User.socialSignUp(email, socialEmail, socialProvider, socialId));

            // when
            Optional<User> result = userRepository.findBySocialProviderAndSocialId(socialProvider, socialId);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result.get().getEmail()).isEqualTo(email);
            assertThat(result.get().getSocialProvider()).isEqualTo(socialProvider);
            assertThat(result.get().getSocialId()).isEqualTo(socialId);
        }

        @Test
        @DisplayName("소셜 로그인 회원이 존재하지 않으면 Empty를 반환한다")
        void findBySocialProviderAndSocialId_NotExists_ReturnsEmpty() {
            // given
            final SocialProvider socialProvider = SocialProvider.GOOGLE;
            final String socialId = "testSocialId";

            // when
            Optional<User> result = userRepository.findBySocialProviderAndSocialId(socialProvider, socialId);

            // then
            assertThat(result).isEmpty();
        }
    }
}