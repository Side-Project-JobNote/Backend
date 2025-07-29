package com.jobnote.auth.security;

import com.jobnote.auth.security.dto.CustomUserDetails;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final User user = userRepository.findByEmail(username)
                .orElse(null);

        if (user != null) {
            return new CustomUserDetails(user);
        }

        return null;
    }
}
