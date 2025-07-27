package com.jobnote.domain.user;

import com.jobnote.common.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jobnote.common.api.ResponseCode.NOT_FOUND_USER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(final Long id) {
        return getByIdOrThrow(id);
    }

    public User getUserByEmail(final String email) {
        return getByEmailOrThrow(email);
    }

    /* HELPER METHOD */
    private User getByIdOrThrow(final Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new JobNoteException(NOT_FOUND_USER));
    }

    private User getByEmailOrThrow(final String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new JobNoteException(NOT_FOUND_USER));
    }
}
