package com.jobnote.domain.user.service;

import com.jobnote.auth.token.Token;
import com.jobnote.domain.common.Time;
import com.jobnote.domain.email.domain.VerificationEmailType;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.email.domain.VerificationEmail;
import com.jobnote.domain.user.dto.*;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.domain.email.service.VerificationEmailService;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jobnote.global.common.ResponseCode.*;
import static com.jobnote.global.common.ResponseCode.DUPLICATED_USER_NICKNAME;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationEmailService verificationEmailService;
    private final AuthTokenService authTokenService;
    private final UserQueryService userQueryService;
    private final Time time;

    public User getUserById(final Long id) {
        return getByIdOrThrow(id);
    }

    /* SIGN UP */
    @Transactional
    public void signUp(final UserSignUpRequest request) {
        validateDuplicatedEmail(request.email());
        validateDuplicatedNickname(request.nickname());
        final User savedUser = userRepository.save(User.signUp(request.email(), passwordEncoder.encode(request.password()), request.nickname()));
        verificationEmailService.send(savedUser, VerificationEmailType.SIGN_UP);
    }

    /* SOCIAL LOGIN SIGN UP */
    @Transactional
    public Token socialSignUp(final SocialSignUpRequest request) {
        validateDuplicatedNickname(request.nickname());
        final User user = userQueryService.getUserByEmail(request.email());
        user.acceptSocial(request.nickname());
        return authTokenService.saveAndGetToken(user.getId());
    }

    /* GET PROFILE */
    public UserProfileResponse getProfile(final Long userId) {
        final User user = userQueryService.getUserById(userId);
        return UserProfileResponse.from(user);
    }

    /* UPDATE PROFILE */
    @Transactional
    public UserProfileResponse updateAvatar(final Long userId, final UserAvatarRequest request) {
        final User user = userQueryService.getUserById(userId);
        user.updateAvatar(request.avatarUrl());
        return UserProfileResponse.from(user);
    }

    @Transactional
    public UserProfileResponse updateNickname(final Long userId, final UserNicknameRequest request) {
        validateDuplicatedNickname(request.nickname());
        final User user = userQueryService.getUserById(userId);
        user.updateNickname(request.nickname());
        return UserProfileResponse.from(user);
    }

    /* RESET PASSWORD */
    @Transactional
    public void resetPassword(final UserResetPasswordRequest request, final String token) {
        final VerificationEmail verificationEmail = verificationEmailService.validateVerified(token);
        final User user = verificationEmail.getUser();
        user.resetPassword(passwordEncoder.encode(request.newPassword()));
    }

    /* WITHDRAW */
    @Transactional
    public void withdraw(final Long userId) {
        final User user = userQueryService.getUserById(userId);
        user.withdraw(this.time.now());
    }

    /* HELPER METHOD */
    private User getByIdOrThrow(final Long id) {
        return userRepository.findById(id)
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
