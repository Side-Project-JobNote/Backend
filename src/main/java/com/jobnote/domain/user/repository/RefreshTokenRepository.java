package com.jobnote.domain.user.repository;

import com.jobnote.domain.user.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByToken(final String token);
    void deleteByToken(final String token);
}
