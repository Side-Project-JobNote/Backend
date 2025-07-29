package com.jobnote.domain.user.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.dto.UserSignUpRequest;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
    public void signUp(final UserSignUpRequest request) {
        validateDuplicatedEmail(request.email());
        validateDuplicatedNickname(request.nickname());
        userRepository.save(User.signUp(request.email(), bCryptPasswordEncoder.encode(request.password()), request.nickname()));
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
