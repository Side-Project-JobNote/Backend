package com.jobnote.domain.user.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserFixture;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.global.exception.JobNoteException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

class UserQueryServiceTest extends ServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryService userQueryService;

    @Nested
    @DisplayName("id로 회원 조회")
    class GetUserById {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final Long userId = 1L;
            final String email = "testEmail@test.com";
            final String password = "testPassword";
            final String nickname = "testNickname";
            final User user = UserFixture.createMember(email, password, nickname);

            given(userRepository.findById(userId)).willReturn(Optional.of(user));

            // when
            final User result = userQueryService.getUserById(userId);

            // then
            assertThat(result).isEqualTo(user);
        }

        @Test
        @DisplayName("실패 - 회원이 존재하지 않는다.")
        void fail_NotExistingUser() {
            // given
            final Long userId = 1L;
            given(userRepository.findById(userId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userQueryService.getUserById(userId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_USER.getMessage());
        }
    }

    @Nested
    @DisplayName("이메일로 회원 조회")
    class GetUserByEmail {
        @Test
        @DisplayName("성공")
        void success() {
            // given
            final String email = "testEmail@test.com";
            final String password = "testPassword";
            final String nickname = "testNickname";
            final User user = UserFixture.createMember(email, password, nickname);

            given(userRepository.findByEmail(email)).willReturn(Optional.of(user));

            // when
            final User result = userQueryService.getUserByEmail(email);

            // then
            assertThat(result).isEqualTo(user);
        }

        @Test
        @DisplayName("실패 - 회원이 존재하지 않는다.")
        void fail_NotExistingUser() {
            // given
            final String email = "testEmail@test.com";
            given(userRepository.findByEmail(email)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userQueryService.getUserByEmail(email))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_USER.getMessage());
        }
    }
}