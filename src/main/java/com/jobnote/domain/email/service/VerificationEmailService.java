package com.jobnote.domain.email.service;

import com.jobnote.domain.email.domain.VerificationEmailType;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.email.domain.VerificationEmail;
import com.jobnote.domain.email.repository.VerificationEmailRepository;
import com.jobnote.domain.user.event.EmailVerificationEvent;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    public VerificationEmail getVerificationEmailByToken(final String token) {
        return getByTokenOrThrow(token);
    }

    /* VERIFY */
    @Transactional
    public VerificationEmail verify(final String token, final LocalDateTime currentDate) {
        final VerificationEmail verificationEmail = getVerificationEmailByToken(token);

        verificationEmail.validateExpired(currentDate);
        verificationEmail.verify();

        return verificationEmail;
    }

    /* SEND */
    @Transactional
    public void send(final User user, final LocalDateTime emailVerificationExpiryDate, final VerificationEmailType type) {
        final VerificationEmail savedVerificationEmail = verificationEmailRepository.save(VerificationEmail.create(UUID.randomUUID().toString(), user, emailVerificationExpiryDate));
        eventPublisher.publishEvent(EmailVerificationEvent.of(user.getEmail(), savedVerificationEmail.getToken(), type));
    }

    /* VALIDATE */
    public VerificationEmail validateVerified(final String token) {
        final VerificationEmail verificationEmail = getVerificationEmailByToken(token);
        verificationEmail.validateVerified();
        return verificationEmail;
    }

    private VerificationEmail getByTokenOrThrow(final String token) {
        return verificationEmailRepository.findByToken(token)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_VERIFICATION_EMAIL));
    }
}
