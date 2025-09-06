package com.jobnote.domain.code.domain;

import com.jobnote.global.exception.JobNoteException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

import static com.jobnote.global.common.ResponseCode.EXPIRED_TEMP_CODE;

@Entity
@Getter
@Table(name = "temp_codes")
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TempCode {

    @Id
    private String code;

    @Column(nullable = false)
    private long userId;

    @Column(nullable = false)
    private LocalDateTime expireAt;

    public static TempCode of(final String code, final long userId, final LocalDateTime expireAt) {
        return TempCode.builder()
                .code(code)
                .userId(userId)
                .expireAt(expireAt)
                .build();
    }

    public void validateExpiration(final LocalDateTime now) {
        if (expireAt.isBefore(now)) {
            throw new JobNoteException(EXPIRED_TEMP_CODE);
        }
    }
}
