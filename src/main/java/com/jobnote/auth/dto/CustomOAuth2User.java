package com.jobnote.auth.dto;

import com.jobnote.domain.user.domain.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

@Getter
public class CustomOAuth2User extends CustomPrincipal implements OAuth2User {

    private final User user;
    private final OAuth2Attributes oAuth2Attributes;

    public CustomOAuth2User(final User user, final OAuth2Attributes oAuth2Attributes) {
        super(user.getId(), user.getEmail());
        this.user = user;
        this.oAuth2Attributes = oAuth2Attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attributes.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey()));
    }

    @Override
    public String getRole() {
        return getAuthorities().iterator().next().getAuthority();
    }

    @Override
    public String getName() {
        return oAuth2Attributes.getProvider() + oAuth2Attributes.getProviderId();
    }
}
