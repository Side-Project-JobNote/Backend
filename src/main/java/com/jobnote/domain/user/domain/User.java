package com.jobnote.domain.user.domain;

import com.jobnote.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "users")
@Builder(access = AccessLevel.PRIVATE)
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

    private String socialEmail;

    @Enumerated(EnumType.STRING)
    private SocialProvider socialProvider;

    private String socialId;

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
}
