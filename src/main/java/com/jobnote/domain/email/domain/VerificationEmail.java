package com.jobnote.domain.email.domain;

import com.jobnote.domain.user.domain.User;
import com.jobnote.global.exception.JobNoteException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.jobnote.global.common.ResponseCode.*;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "verification_tokens")
@Entity
public class VerificationEmail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_token_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, updatable = false)
    private LocalDateTime expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationEmailStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationEmailType type;

    public static VerificationEmail create(final String token, final User user, final LocalDateTime expiryDate, final VerificationEmailType type) {
        return VerificationEmail.builder()
                .token(token)
                .user(user)
                .expiryDate(expiryDate)
                .status(VerificationEmailStatus.PENDING)
                .type(type)
                .build();
    }

    public void validateExpired(final LocalDateTime currentDate) {
        if (VerificationEmailStatus.EXPIRED.equals(this.status) || this.expiryDate.isBefore(currentDate)) {
            this.status = VerificationEmailStatus.EXPIRED;
            throw new JobNoteException(EXPIRED_VERIFICATION_EMAIL);
        }
    }

    public void validateVerified() {
        if (!VerificationEmailStatus.VERIFIED.equals(this.status)) {
            throw new JobNoteException(VERIFICATION_EMAIL_NOT_YET_VERIFIED);
        }
    }

    public void verify() {
        if (VerificationEmailStatus.VERIFIED.equals(this.status)) {
            throw new JobNoteException(VERIFICATION_EMAIL_ALREADY_VERIFIED);
        }
        this.status = VerificationEmailStatus.VERIFIED;
    }
}
