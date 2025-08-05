package com.jobnote.domain.user.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.dto.SocialSignUpRequest;
import com.jobnote.domain.user.dto.UserSignUpRequest;
import com.jobnote.domain.user.service.AuthTokenService;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.jobnote.global.common.Constants.COOKIE_NAME_REFRESH_TOKEN;
import static com.jobnote.global.util.CookieUtil.getTokenFromCookie;

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

    /* SOCIAL LOGIN SIGN UP */
    @PostMapping("/signup/social")
    public ResponseEntity<ApiResponse<Void>> socialSignUp(@RequestBody @Valid final SocialSignUpRequest request, @LoginUser final CustomPrincipal principal) {
        userService.socialSignUp(request, principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* EMAIL VERIFICATION */
    @GetMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(@RequestParam("token") final String token) {
        userService.verifyEmail(token, LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* TOKEN REISSUE */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> tokenReissue(@LoginUser CustomPrincipal principal, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Token token = authTokenService.reissue(principal.getUserId(), getTokenFromCookie(request.getCookies(), COOKIE_NAME_REFRESH_TOKEN));

        tokenProvider.responseToken(response, token);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
