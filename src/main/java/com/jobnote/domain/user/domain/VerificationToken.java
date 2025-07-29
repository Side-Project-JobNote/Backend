package com.jobnote.domain.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "verification_tokens")
@Entity
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_token_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expiryDate;

    public static VerificationToken create(final String token, final User user, final LocalDateTime expiryDate) {
        return VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .build();
    }

    public boolean validateExpiration(final LocalDateTime currentDate) {
        return expiryDate.isBefore(currentDate);
    }
}
