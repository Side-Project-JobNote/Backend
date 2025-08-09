package com.jobnote.domain.email.controller;

import com.jobnote.domain.email.dto.VerificationEmailRequest;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1/verification-emails")
@RestController
public class VerificationEmailController {

    private final UserService userService;

    /* SEND VERIFICATION EMAIL */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@RequestBody @Valid final VerificationEmailRequest request) {
        userService.sendVerificationEmail(request, LocalDateTime.now().plusDays(1));
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* VERIFY VERIFICATION EMAIL */
    @GetMapping("/signup/verify")
    public ResponseEntity<ApiResponse<Void>> verifySignUpEmail(@RequestParam("token") final String token) {
        userService.verifySignUp(token, LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    @GetMapping("/reset-password/verify")
    public ResponseEntity<ApiResponse<Void>> verifyResetPasswordEmail(@RequestParam("token") final String token) {
        userService.verifyEmail(token, LocalDateTime.now());
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}