package com.jobnote.domain.user.domain;

import com.jobnote.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Builder
    public User(
            final String password,
            final String email,
            final String nickname,
            final String avatarUrl,
            final UserRole role
    ) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.avatarUrl = avatarUrl;
        this.role = role;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
