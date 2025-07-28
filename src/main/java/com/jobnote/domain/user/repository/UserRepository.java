package com.jobnote.domain.user.repository;

import com.jobnote.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(final String email);
    boolean existsByNickname(final String nickname);
}
