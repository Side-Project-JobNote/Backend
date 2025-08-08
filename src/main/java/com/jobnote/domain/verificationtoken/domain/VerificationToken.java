package com.jobnote.domain.verificationtoken.domain;

import com.jobnote.domain.user.domain.User;
import com.jobnote.global.exception.JobNoteException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.jobnote.global.common.ResponseCode.ALREADY_VERIFIED_TOKEN;
import static com.jobnote.global.common.ResponseCode.EXPIRED_VERIFICATION_TOKEN;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationTokenStatus status;

    public static VerificationToken create(final String token, final User user, final LocalDateTime expiryDate) {
        return VerificationToken.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .status(VerificationTokenStatus.PENDING)
                .build();
    }

    public void validateExpired(final LocalDateTime currentDate) {
        if (VerificationTokenStatus.EXPIRED.equals(this.status) || this.expiryDate.isBefore(currentDate)) {
            this.status = VerificationTokenStatus.EXPIRED;
            throw new JobNoteException(EXPIRED_VERIFICATION_TOKEN);
        }
    }

    public void validateVerified() {
        if (VerificationTokenStatus.VERIFIED.equals(this.status)) {
            throw new JobNoteException(ALREADY_VERIFIED_TOKEN);
        }
    }

    public void complete() {
        this.status = VerificationTokenStatus.VERIFIED;
    }
}
