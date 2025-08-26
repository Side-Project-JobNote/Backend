package com.jobnote.auth.dto;

import com.jobnote.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails, OAuth2User {

    private final User user;
    private final OAuth2Attributes oAuth2Attributes;

    public CustomUserDetails(User user) {
        this(user, null);
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey()));
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attributes.getAttributes();
    }

    @Override
    public String getName() {
        return oAuth2Attributes.getProvider() + oAuth2Attributes.getProviderId();
    }

    @Override
    public boolean isEnabled() {
        return !user.isDeleted();
    }

    public String getRole() {
        return getAuthorities().iterator().next().getAuthority();
    }

    public Long getUserId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }
}
