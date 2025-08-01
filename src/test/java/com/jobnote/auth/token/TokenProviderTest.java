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
        TokenClaim tokenClaim = TokenClaim.builder()
                .email("testEmail@email.com")
                .build();

        // when
        Token token = tokenProvider.issueToken(tokenClaim);

        // then
        verify(jwtProvider, times(1)).generateAccessToken(tokenClaim);
        verify(jwtProvider, times(1)).generateRefreshToken();
    }
}