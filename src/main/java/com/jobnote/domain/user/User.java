package com.jobnote.domain.user;

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
    private String name;

    private String avatarUrl;

    @Builder
    public User(
            String password,
            String email,
            String name,
            String avatarUrl
    ) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.avatarUrl = avatarUrl;
    }
}
