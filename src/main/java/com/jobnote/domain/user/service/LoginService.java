package com.jobnote.domain.user.service;

import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.auth.token.Token;
import com.jobnote.domain.user.domain.UserRole;
import com.jobnote.domain.user.dto.UserLoginRequest;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jobnote.global.common.ResponseCode.INVALID_USERNAME_PASSWORD;
import static com.jobnote.global.common.ResponseCode.PENDING_EMAIL_VERIFICATION;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class LoginService {

    private final AuthenticationManager authenticationManager;
    private final AuthTokenService authTokenService;

    /* LOGIN */
    public Token login(final UserLoginRequest request) {
        final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(request.email(), request.password());

        try {
            final Authentication authentication = authenticationManager.authenticate(authenticationToken);
            final CustomPrincipal principal = (CustomPrincipal) authentication.getPrincipal();

            if (UserRole.GUEST.getKey().equals(principal.getRole())) {
                throw new JobNoteException(PENDING_EMAIL_VERIFICATION);
            }

            return authTokenService.saveAndGetToken(principal.getUserId());
        } catch (BadCredentialsException e) {
            throw new JobNoteException(INVALID_USERNAME_PASSWORD);
        }
    }
}
