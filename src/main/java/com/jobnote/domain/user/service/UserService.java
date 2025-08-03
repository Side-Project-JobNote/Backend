package com.jobnote.domain.user.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.VerificationToken;
import com.jobnote.domain.user.dto.UserSignUpRequest;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.domain.user.repository.VerificationTokenRepository;
import com.jobnote.domain.user.dto.SignUpEvent;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.jobnote.global.common.ResponseCode.*;
import static com.jobnote.global.common.ResponseCode.DUPLICATED_USER_NICKNAME;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public User getUserById(final Long id) {
        return getByIdOrThrow(id);
    }

    public User getUserByEmail(final String email) {
        return getByEmailOrThrow(email);
    }

    public Long getUserIdFromUserDetails(final UserDetails user) {
        return getUserByEmail(user.getUsername()).getId();
    }

    /* SIGN UP */
    @Transactional
    public void signUp(final UserSignUpRequest request, final LocalDateTime verificationExpiryDate) {
        validateDuplicatedEmail(request.email());
        validateDuplicatedNickname(request.nickname());
        User savedUser = userRepository.save(User.signUp(request.email(), passwordEncoder.encode(request.password()), request.nickname()));
        VerificationToken savedVerificationToken = verificationTokenRepository.save(VerificationToken.create(UUID.randomUUID().toString(), savedUser, verificationExpiryDate));

        eventPublisher.publishEvent(new SignUpEvent(savedUser.getEmail(), savedVerificationToken.getToken()));
    }

    /* EMAIL VERIFICATION */
    @Transactional
    public void verifyEmail(final String token, final LocalDateTime currentDate) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_VERIFICATION_TOKEN));

        if (verificationToken.validateExpiration(currentDate)) {
            throw new JobNoteException(EXPIRED_VERIFICATION_TOKEN);
        }

        verificationToken.getUser().accept();
    }

    /* HELPER METHOD */
    public User getByIdOrThrow(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_USER));
    }

    public User getByEmailOrThrow(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_USER));
    }

    private void validateDuplicatedNickname(final String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new JobNoteException(DUPLICATED_USER_NICKNAME);
        }
    }

    private void validateDuplicatedEmail(final String email) {
        if (userRepository.existsByEmail(email)) {
            throw new JobNoteException(DUPLICATED_USER_EMAIL);
        }
    }
}
