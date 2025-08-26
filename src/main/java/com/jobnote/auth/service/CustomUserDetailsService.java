package com.jobnote.auth.service;

import com.jobnote.auth.dto.CustomUserDetailsOld;
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
                .orElseThrow(() -> new UsernameNotFoundException("아이디를 찾을 수 없습니다."));

        return new CustomUserDetailsOld(user);
    }
}
