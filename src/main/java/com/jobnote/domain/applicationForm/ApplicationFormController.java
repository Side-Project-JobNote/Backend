package com.jobnote.domain.applicationForm;

import com.jobnote.common.api.ApiResponse;
import com.jobnote.common.api.ResponseCode;
import com.jobnote.domain.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/application-forms")
@RequiredArgsConstructor
public class ApplicationFormController {

    private final ApplicationFormService applicationFormService;
    private final UserService userService;

    /* CREATE */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createApplicationForm(
            @RequestBody @Valid ApplicationFormRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        //todo 추후 CustomUserDetails 구현 후 User id 값으로 받아올 예정
        Long userId = getUserIdFromUserDetails(user);
        Long savedFormId = applicationFormService.save(userId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedFormId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* READ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationFormResponse>> getApplicationForm(
            @PathVariable("id") Long formId,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long userId = getUserIdFromUserDetails(user);
        ApplicationFormResponse form = applicationFormService.getById(userId, formId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, form));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ApplicationFormResponse>>> getAllApplicationForms(
            @AuthenticationPrincipal UserDetails user
    ) {
        Long userId = getUserIdFromUserDetails(user);
        List<ApplicationFormResponse> forms = applicationFormService.getAll(userId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, forms));
    }

    /* UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateApplicationForm(
            @PathVariable("id") Long formId,
            @Valid @RequestBody ApplicationFormRequest request,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long userId = getUserIdFromUserDetails(user);
        applicationFormService.update(userId, formId, request);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplicationForm(
            @PathVariable("id") Long formId,
            @AuthenticationPrincipal UserDetails user
    ) {
        Long userId = getUserIdFromUserDetails(user);
        applicationFormService.delete(userId, formId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    private Long getUserIdFromUserDetails(UserDetails user) {
        return userService.getUserByLoginId(user.getUsername()).getId();
    }
}
