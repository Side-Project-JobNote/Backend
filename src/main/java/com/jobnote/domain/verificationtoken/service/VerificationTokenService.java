package com.jobnote.domain.verificationtoken.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.verificationtoken.domain.VerificationToken;
import com.jobnote.domain.verificationtoken.repository.VerificationTokenRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_VERIFICATION_TOKEN;

@RequiredArgsConstructor
@Service
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationToken getVerificationTokenByToken(final String token) {
        return getByTokenOrThrow(token);
    }

    /* CREATE */
    public VerificationToken save(final User user, final LocalDateTime emailVerificationExpiryDate) {
        return verificationTokenRepository.save(VerificationToken.create(UUID.randomUUID().toString(), user, emailVerificationExpiryDate));
    }

    private VerificationToken getByTokenOrThrow(final String token) {
        return verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_VERIFICATION_TOKEN));
    }
}
