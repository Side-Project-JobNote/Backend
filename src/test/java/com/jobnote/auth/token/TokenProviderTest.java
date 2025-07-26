package com.jobnote.auth.token;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TokenProviderTest {

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private TokenProvider tokenProvider;

    @Test
    @DisplayName("JWT 토큰 생성에 성공한다")
    void generateToken_Success() {
        // given
        final long userId = 1L;
        final String role = "ROLE_USER";

        // when
        Token token = tokenProvider.issueToken(userId, role);

        // then
        verify(jwtProvider, times(1)).generateAccessToken(userId, role);
        verify(jwtProvider, times(1)).generateRefreshToken();
    }
}