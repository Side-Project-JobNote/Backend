package com.jobnote.domain.user.api;

import com.jobnote.domain.user.dto.UserSignUpRequest;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
public class UserController {

    private final UserService userService;

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
}
