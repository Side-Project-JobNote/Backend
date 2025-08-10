package com.jobnote.domain.verificationtoken.repository;

import com.jobnote.domain.verificationtoken.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(final String token);
}
