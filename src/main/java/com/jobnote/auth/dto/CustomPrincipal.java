package com.jobnote.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@RequiredArgsConstructor
public abstract class CustomPrincipal {

    private final Long userId;
    private final String email;

    public abstract Collection<? extends GrantedAuthority> getAuthorities();

    public abstract String getRole();
}
