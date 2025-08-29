package com.jobnote.domain.email.controller;

import com.jobnote.domain.email.dto.VerificationEmailRequest;
import com.jobnote.domain.email.service.VerificationEmailService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.global.config.properties.FrontendProperties;
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

    private final VerificationEmailService verificationEmailService;
    private final FrontendProperties frontendProperties;

    /* SEND VERIFICATION EMAIL */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> sendVerificationEmail(@RequestBody @Valid final VerificationEmailRequest request) {
        verificationEmailService.send(request);
        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* VERIFY VERIFICATION EMAIL */
    @GetMapping("/signup/verify")
    public void verifySignUpEmail(@RequestParam("token") final String token, final HttpServletResponse response) throws IOException {
        verificationEmailService.verifySignUp(token);
        response.sendRedirect(frontendProperties.baseUrl() + frontendProperties.mainPage());
    }

    @GetMapping("/reset-password/verify")
    public void verifyResetPasswordEmail(@RequestParam("token") final String token, final HttpServletResponse response) throws IOException {
        verificationEmailService.verify(token);
        response.sendRedirect(frontendProperties.baseUrl() + frontendProperties.resetPasswordPage() + "?token=" + token);
    }
}