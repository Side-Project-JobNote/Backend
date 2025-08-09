package com.jobnote.domain.email.repository;

import com.jobnote.domain.email.domain.VerificationEmail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VerificationEmailRepository extends JpaRepository<VerificationEmail, Long> {
    Optional<VerificationEmail> findByToken(final String token);
}
