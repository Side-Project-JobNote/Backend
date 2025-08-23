package com.jobnote.domain.email.controller;

import com.jobnote.domain.email.dto.VerificationEmailRequest;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.util.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequiredArgsConstructor
@RequestMapping("/api/v1/verification-emails")
@RestController
public class VerificationEmailController {

    private final UserService userService;

    /* SEND VERIFICATION EMAIL */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@RequestBody @Valid final VerificationEmailRequest request) {
        userService.sendVerificationEmail(request);
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* VERIFY VERIFICATION EMAIL */
    @GetMapping("/signup/verify")
    public void verifySignUpEmail(@RequestParam("token") final String token, final HttpServletResponse response) throws IOException {
        userService.verifySignUp(token);
        ResponseUtil.redirectToFrontend(response);
    }

    @GetMapping("/reset-password/verify")
    public void verifyResetPasswordEmail(@RequestParam("token") final String token, final HttpServletResponse response) throws IOException {
        userService.verifyEmail(token);
        ResponseUtil.redirectToFrontend(response);
    }
}