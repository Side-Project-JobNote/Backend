package com.jobnote.domain.user.service;

import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenClaim;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.domain.RefreshToken;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.repository.RefreshTokenRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.jobnote.global.common.Constants.CLAIM_VALUE_REFRESH_TOKEN;
import static com.jobnote.global.common.ResponseCode.NOT_FOUND_REFRESH_TOKEN;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AuthTokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Transactional
    public Token issue(final Long userId) {
        final User user = userService.getUserById(userId);
        final Token token = tokenProvider.issueToken(TokenClaim.builder().email(user.getEmail()).build());
        final LocalDateTime expiration = tokenProvider.getExpiration(token.refreshToken(), CLAIM_VALUE_REFRESH_TOKEN);
        refreshTokenRepository.save(RefreshToken.of(user, token.refreshToken(), expiration));

        return token;
    }

    @Transactional
    public Token reissue(final Long userId, final String existingRefreshToken) {
        invalidate(existingRefreshToken);
        return issue(userId);
    }

    @Transactional
    public void invalidate(final String targetRefreshToken) {
        validateExistsRefreshToken(targetRefreshToken);
        refreshTokenRepository.deleteByToken(targetRefreshToken);
    }

    private void validateExistsRefreshToken(final String existingRefreshToken) {
        if (!refreshTokenRepository.existsByToken(existingRefreshToken)) {
            throw new JobNoteException(NOT_FOUND_REFRESH_TOKEN);
        }
    }
}
