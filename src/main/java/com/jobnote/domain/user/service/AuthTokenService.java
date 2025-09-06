package com.jobnote.domain.user.service;

import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenClaim;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.code.service.TempCodeService;
import com.jobnote.domain.refreshtoken.domain.RefreshToken;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.refreshtoken.repository.RefreshTokenRepository;
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
    private final UserQueryService userQueryService;
    private final TempCodeService tempCodeService;

    @Transactional
    public Token saveAndGetToken(final Long userId) {
        final User user = userQueryService.getUserById(userId);
        final Token token = issueToken(user.getEmail());
        final LocalDateTime expiration = tokenProvider.getExpiration(token.refreshToken(), CLAIM_VALUE_REFRESH_TOKEN);
        refreshTokenRepository.save(RefreshToken.of(user, token.refreshToken(), expiration));

        return token;
    }

    @Transactional
    public Token reissue(final String existingRefreshToken) {
        tokenProvider.validateRefreshToken(existingRefreshToken);

        final RefreshToken refreshToken = getRefreshTokenByToken(existingRefreshToken);
        refreshTokenRepository.delete(refreshToken);

        return saveAndGetToken(refreshToken.getUser().getId());
    }

    @Transactional
    public Token issueByCode(final String code) {
        final long userId = tempCodeService.validate(code);
        return saveAndGetToken(userId);
    }

    @Transactional
    public void invalidate(final String targetRefreshToken) {
        validateExistsRefreshToken(targetRefreshToken);
        refreshTokenRepository.deleteByToken(targetRefreshToken);
    }

    private Token issueToken(final String email) {
        final TokenClaim tokenClaim = TokenClaim.builder()
                .email(email)
                .build();
        return tokenProvider.issueToken(tokenClaim);
    }

    private void validateExistsRefreshToken(final String existingRefreshToken) {
        if (!refreshTokenRepository.existsByToken(existingRefreshToken)) {
            throw new JobNoteException(NOT_FOUND_REFRESH_TOKEN);
        }
    }

    private RefreshToken getRefreshTokenByToken(final String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_REFRESH_TOKEN));
    }
}
