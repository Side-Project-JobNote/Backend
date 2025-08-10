package com.jobnote.domain.user.controller;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.auth.token.Token;
import com.jobnote.auth.token.TokenProvider;
import com.jobnote.domain.user.dto.*;
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

    /* TOKEN REISSUE */
    @PostMapping("/reissue")
    public ResponseEntity<ApiResponse<Void>> tokenReissue(@LoginUser CustomPrincipal principal, final HttpServletRequest request, final HttpServletResponse response) throws IOException {
        final Token token = authTokenService.reissue(principal.getUserId(), getTokenFromCookie(request.getCookies(), COOKIE_NAME_REFRESH_TOKEN));

        tokenProvider.responseToken(response, token);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* GET PROFILE */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@LoginUser final CustomPrincipal principal) {
        final UserProfileResponse response = userService.getProfile(principal.getUserId());
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, response));
    }

    /* UPDATE PROFILE */
    @PatchMapping("/avatar")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateAvatar(@LoginUser final CustomPrincipal principal, @RequestBody @Valid final UserAvatarRequest request) {
        final UserProfileResponse response = userService.updateAvatar(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, response));
    }

    @PatchMapping("/nickname")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateNickname(@LoginUser final CustomPrincipal principal, @RequestBody @Valid final UserNicknameRequest request) {
        final UserProfileResponse response = userService.updateNickname(principal.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, response));
    }

    /* RESET PASSWORD */
    @PatchMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody @Valid final UserResetPasswordRequest request, @RequestParam("token") final String token) {
        userService.resetPassword(request, token);
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}