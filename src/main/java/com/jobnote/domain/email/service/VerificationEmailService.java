package com.jobnote.domain.email.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.email.domain.VerificationEmail;
import com.jobnote.domain.email.repository.VerificationEmailRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_VERIFICATION_EMAIL;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class VerificationEmailService {

    private final VerificationEmailRepository verificationEmailRepository;

    public VerificationEmail getVerificationEmailByToken(final String token) {
        return getByTokenOrThrow(token);
    }

    @Transactional
    public VerificationEmail verifyToken(final String token, final LocalDateTime currentDate) {
        final VerificationEmail verificationEmail = getVerificationEmailByToken(token);

        verificationEmail.validateExpired(currentDate);
        verificationEmail.verify();

        return verificationEmail;
    }

    /* CREATE */
    @Transactional
    public VerificationEmail save(final User user, final LocalDateTime emailVerificationExpiryDate) {
        return verificationEmailRepository.save(VerificationEmail.create(UUID.randomUUID().toString(), user, emailVerificationExpiryDate));
    }

    private VerificationEmail getByTokenOrThrow(final String token) {
        return verificationEmailRepository.findByToken(token)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_VERIFICATION_EMAIL));
    }
}
