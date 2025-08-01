package com.jobnote.domain.user.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.dto.UserSignUpRequest;
import com.jobnote.domain.user.service.AuthTokenService;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.exception.JobNoteException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.jobnote.global.common.Constants.COOKIE_NAME_REFRESH_TOKEN;
import static com.jobnote.global.common.ResponseCode.INVALID_TOKEN;
import static com.jobnote.global.util.CookieUtil.getCookie;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;
    private final AuthTokenService authTokenService;
    private final TokenProvider tokenProvider;

    /* SIGN UP */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody @Valid final UserSignUpRequest request) {
        userService.signUp(request, LocalDateTime.now().plusDays(1));
        return ResponseEntity.status(ResponseCode.CREATED.getStatus()).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* EMAIL VERIFICATION */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") final String token) {
        userService.verifyEmail(token, LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* TOKEN REISSUE */
    @GetMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> tokenReissue(@LoginUser CustomUserDetails userDetails, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final String refreshToken = getCookie(request, COOKIE_NAME_REFRESH_TOKEN)
                .orElseThrow(() -> new JobNoteException(INVALID_TOKEN))
                .getValue();

        final Token token = authTokenService.reissue(userDetails.getUserId(), refreshToken);

        tokenProvider.responseToken(response, token);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
