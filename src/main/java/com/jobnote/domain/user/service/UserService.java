package com.jobnote.domain.user.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.verificationtoken.domain.VerificationToken;
import com.jobnote.domain.user.dto.*;
import com.jobnote.domain.user.event.SignUpEvent;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.domain.verificationtoken.service.VerificationTokenService;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.jobnote.global.common.ResponseCode.*;
import static com.jobnote.global.common.ResponseCode.DUPLICATED_USER_NICKNAME;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final VerificationTokenService verificationTokenService;
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
    public void signUp(final UserSignUpRequest request, final LocalDateTime emailVerificationExpiryDate) {
        validateDuplicatedEmail(request.email());
        validateDuplicatedNickname(request.nickname());
        final User savedUser = userRepository.save(User.signUp(request.email(), passwordEncoder.encode(request.password()), request.nickname()));
        final VerificationToken savedVerificationToken = verificationTokenService.save(savedUser, emailVerificationExpiryDate);

        eventPublisher.publishEvent(new SignUpEvent(savedUser.getEmail(), savedVerificationToken.getToken()));
    }

    /* SOCIAL LOGIN SIGN UP */
    @Transactional
    public void socialSignUp(final SocialSignUpRequest request, final Long userId) {
        validateDuplicatedNickname(request.nickname());
        final User user = getUserById(userId);
        user.acceptSocial(request.nickname());
    }

    /* EMAIL VERIFICATION */
    @Transactional
    public void verifyEmail(final String token, final LocalDateTime currentDate) {
        final VerificationToken verificationToken = verificationTokenService.getVerificationTokenByToken(token);
        final User user = verificationToken.getUser();

        verificationToken.validateExpired(currentDate);
        verificationToken.validateVerified();

        user.accept();
        verificationToken.complete();
    }

    /* GET PROFILE */
    public UserProfileResponse getProfile(final Long userId) {
        final User user = getUserById(userId);
        return UserProfileResponse.from(user);
    }

    /* UPDATE PROFILE */
    @Transactional
    public UserProfileResponse updateAvatar(final Long userId, final UserAvatarRequest request) {
        final User user = getUserById(userId);
        user.updateAvatar(request.avatarUrl());
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateNickname(final Long userId, final UserNicknameRequest request) {
        validateDuplicatedNickname(request.nickname());
        final User user = getUserById(userId);
        user.updateNickname(request.nickname());
        return UserProfileResponse.from(user);
    }

    /* HELPER METHOD */
    private User getByIdOrThrow(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_USER));
    }

    private User getByEmailOrThrow(final String email) {
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
