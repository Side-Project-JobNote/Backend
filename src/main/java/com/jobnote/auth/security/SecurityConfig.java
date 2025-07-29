package com.jobnote.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.domain.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.jobnote.global.common.Constants.WHITELIST;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ObjectMapper objectMapper;

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(final HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable);

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                        .requestMatchers(WHITELIST).permitAll()
                        .requestMatchers("/api/*/admin/**").hasRole(UserRole.ADMIN.name())
                        .anyRequest().hasAnyRole(UserRole.MEMBER.name(), UserRole.ADMIN.name()));

        final LoginFilter loginFilter = new LoginFilter(authenticationManager(authenticationConfiguration), tokenProvider, objectMapper);
        loginFilter.setFilterProcessesUrl("/api/v1/users/login");

        http
                .addFilterBefore(loginFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .addFilterBefore(new TokenAuthenticationFilter(tokenProvider), LoginFilter.class);

        http
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint(objectMapper)));

        http
                .sessionManagement(session -> session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
