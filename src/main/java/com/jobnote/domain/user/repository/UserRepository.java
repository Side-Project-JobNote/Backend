package com.jobnote.domain.user.repository;

import com.jobnote.domain.user.domain.SocialProvider;
import com.jobnote.domain.user.domain.User;
import com.jobnote.global.exception.JobNoteException;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_USER;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(final String email);
    boolean existsByNickname(final String nickname);
    boolean existsByEmail(final String email);
    Optional<User> findBySocialProviderAndSocialId(final SocialProvider socialProvider, final String socialId);

    default User getById(final Long id) {
        return this.findById(id)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_USER));
    }

    default User getByEmail(final String email) {
        return this.findByEmail(email)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_USER));
    }
}
