package com.jobnote.auth.security;

import com.jobnote.global.config.properties.SecurityProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final SecurityProperties securityProperties;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

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
                        .requestMatchers(securityProperties.whitelist().toArray(String[]::new)).permitAll()
                        .anyRequest().authenticated());

        http
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .authenticationEntryPoint(customAuthenticationEntryPoint));

        http
                .sessionManagement(session -> session.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}
