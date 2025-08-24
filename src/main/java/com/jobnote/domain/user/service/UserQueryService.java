package com.jobnote.domain.user.service;

import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.repository.UserRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserQueryService {

    private final UserRepository userRepository;

    public User getUserById(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_USER));
    }

    public User getUserByEmail(final String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_USER));
    }
}
