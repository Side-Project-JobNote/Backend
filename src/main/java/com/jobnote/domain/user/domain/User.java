package com.jobnote.domain.user.domain;

import com.jobnote.domain.common.BaseTimeEntity;
import com.jobnote.global.exception.JobNoteException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.jobnote.global.common.ResponseCode.USER_ALREADY_WITHDRAWN;

@Entity
@Getter
@Table(name = "users")
@Builder(access = AccessLevel.PACKAGE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String nickname;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean isDeleted;

    private String socialEmail;

    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    private String socialId;

    private LocalDateTime deletedDate;

    public static User signUp(final String email, final String password, final String nickname) {
        return User.builder()
                .email(email)
                .password(password)
                .nickname(nickname)
                .role(UserRole.GUEST)
                .build();
    }

    public static User socialSignUp(final String email, final String socialEmail, final SocialProvider socialProvider, final String socialId) {
        return User.builder()
                .email(email)
                .socialEmail(socialEmail)
                .socialProvider(socialProvider)
                .socialId(socialId)
                .role(UserRole.GUEST)
                .build();
    }

    public String getRoleKey() {
        return this.role.getKey();
    }

    public void accept() {
        this.role = UserRole.MEMBER;
    }

    public void acceptSocial(final String nickname) {
        updateNickname(nickname);
        this.accept();
    }

    public void updateAvatar(final String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public void updateNickname(final String nickname) {
        this.nickname = nickname;
    }

    public void resetPassword(final String newPassword) {
        this.password = newPassword;
    }

    public void withdraw(final LocalDateTime deletedDate) {
        if (this.isDeleted) {
            throw new JobNoteException(USER_ALREADY_WITHDRAWN);
        }
        this.isDeleted = true;
        this.deletedDate = deletedDate;
    }
}
