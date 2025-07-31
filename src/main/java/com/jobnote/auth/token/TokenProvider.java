package com.jobnote.auth.token;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.domain.user.dto.UserTokenResponse;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

import static com.jobnote.global.common.Constants.*;

@RequiredArgsConstructor
@Component
public class TokenProvider {

    private final JwtProvider jwtProvider;

    public Token issueToken(final TokenClaim tokenClaim) {
        return Token.builder()
                .accessToken(jwtProvider.generateAccessToken(tokenClaim))
                .refreshToken(jwtProvider.generateRefreshToken())
                .build();
    }

    public Optional<String> getEmailFromPayload(final String token) {
        return Optional.of(jwtProvider.getTokenPayload(token).get(CLAIM_NAME_EMAIL, String.class));
    }

    public void responseToken(final HttpServletResponse response, final ObjectMapper objectMapper, final Long userId, final String email) throws IOException {
        final TokenClaim tokenClaim = TokenClaim.builder()
                .email(email)
                .build();

        final Token token = this.issueToken(tokenClaim);

        final ResponseCode responseCode = ResponseCode.OK;
        response.setStatus(responseCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CHARACTER_ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.ofSuccess(responseCode, UserTokenResponse.of(userId, token))));
    }
}
