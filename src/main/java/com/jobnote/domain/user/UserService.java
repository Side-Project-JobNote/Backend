package com.jobnote.domain.user;

import com.jobnote.common.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jobnote.common.api.ResponseCode.NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserById(Long id) {
        return getByIdOrThrow(id);
    }

    public User getUserByLoginId(String loginId) {
        return getByLoginIdOrThrow(loginId);
    }

    /* HELPER METHOD */
    private User getByIdOrThrow(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new JobNoteException(NOT_FOUND));
    }

    private User getByLoginIdOrThrow(String loginId) {
        return userRepository.findByLoginId(loginId).orElseThrow(() ->
                new JobNoteException(NOT_FOUND));
    }
}
