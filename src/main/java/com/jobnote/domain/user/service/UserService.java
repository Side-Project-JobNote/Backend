package com.jobnote.domain.user.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.VerificationToken;
import com.jobnote.domain.user.dto.*;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.domain.user.repository.VerificationTokenRepository;
import com.jobnote.global.config.properties.AppProperties;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.mail.MailService;
import com.jobnote.mail.dto.MailMessageDto;
import lombok.RequiredArgsConstructor;
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
    private final MailService mailService;
    private final AppProperties appProperties;

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
        User savedUser = userRepository.save(User.signUp(request.email(), passwordEncoder.encode(request.password()), request.nickname()));
        VerificationToken verificationToken = VerificationToken.create(UUID.randomUUID().toString(), savedUser, emailVerificationExpiryDate);
        verificationTokenRepository.save(verificationToken);
        sendVerificationEmail(savedUser.getEmail(), verificationToken);
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
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_VERIFICATION_TOKEN));
        User user = verificationToken.getUser();

        if (verificationToken.validateExpiration(currentDate)) {
            verificationTokenRepository.delete(verificationToken);
            throw new JobNoteException(EXPIRED_VERIFICATION_TOKEN);
        }

        user.accept();
        verificationTokenRepository.delete(verificationToken);
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

    private void sendVerificationEmail(final String email, final VerificationToken verificationToken) {
        final String link = appProperties.baseUrl() + appProperties.emailVerificationPath() + "?token=" + verificationToken.getToken();
        final String subject = "JobNote 회원가입 이메일 인증";
        final String text = String.format("""
                JobNote를 이용해주셔서 감사합니다.
                아래 이메일 인증 링크를 클릭하여 회원가입을 완료해 주세요.
                감사합니다.
                %s
                """, link);
        MailMessageDto mailMessageDto = MailMessageDto.builder()
                .to(email)
                .subject(subject)
                .text(text)
                .build();
        mailService.sendMail(mailMessageDto);
    }
}
