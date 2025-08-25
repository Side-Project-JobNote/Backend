package com.jobnote.domain.refreshtoken.repository;

import com.jobnote.domain.refreshtoken.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByToken(final String token);
    void deleteByToken(final String token);
}
