package com.jobnote.domain.applicationform.controller;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.applicationform.api.ApplicationFormApi;
import com.jobnote.domain.applicationform.dto.ApplicationFormListResponse;
import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.domain.applicationform.dto.ApplicationFormResponse;
import com.jobnote.domain.applicationform.service.ApplicationFormService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/application-forms")
@RequiredArgsConstructor
public class ApplicationFormController implements ApplicationFormApi {

    private final ApplicationFormService applicationFormService;

    /* CREATE */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createApplicationForm(
            @RequestBody @Valid final ApplicationFormRequest request,
            @LoginUser final CustomUserDetails principal
    ) {
        Long savedFormId = applicationFormService.save(principal.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedFormId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* READ */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ApplicationFormResponse>> getApplicationForm(
            @PathVariable("id") final Long formId,
            @LoginUser final CustomUserDetails principal
    ) {
        ApplicationFormResponse form = applicationFormService.getById(principal.getUserId(), formId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, form));
    }

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<ApplicationFormListResponse>> getAllApplicationForms(
            @LoginUser final CustomUserDetails principal
    ) {
        List<ApplicationFormResponse> forms = applicationFormService.getAll(principal.getUserId());
        ApplicationFormListResponse listResponse = ApplicationFormListResponse.from(forms);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, listResponse));
    }

    /* UPDATE */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateApplicationForm(
            @PathVariable("id") final Long formId,
            @Valid @RequestBody final ApplicationFormRequest request,
            @LoginUser final CustomUserDetails principal
    ) {
        applicationFormService.update(principal.getUserId(), formId, request);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* DELETE */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteApplicationForm(
            @PathVariable("id") final Long formId,
            @LoginUser final CustomUserDetails principal
    ) {
        applicationFormService.delete(principal.getUserId(), formId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
