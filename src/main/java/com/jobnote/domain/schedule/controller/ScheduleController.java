package com.jobnote.domain.schedule.controller;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.service.ApplicationFormService;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.service.ScheduleService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/application-forms/{formId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ApplicationFormService applicationFormService;

    /* CREATE */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSchedule(
            @PathVariable Long formId,
            @RequestBody @Valid final ScheduleRequest request,
            @LoginUser final CustomPrincipal principal
    ) {
        ApplicationForm form = applicationFormService.getByIdOrThrow(formId);

        Long savedScheduleId = scheduleService.save(principal.getUserId(), form, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedScheduleId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* READ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @LoginUser final CustomPrincipal principal
    ) {
        ScheduleResponse form = scheduleService.getById(principal.getUserId(), formId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, form));
    }

    /* UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @Valid @RequestBody final ScheduleRequest request,
            @LoginUser final CustomPrincipal principal
    ) {
        scheduleService.update(principal.getUserId(), formId, scheduleId, request);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @LoginUser final CustomPrincipal principal
    ) {
        scheduleService.delete(principal.getUserId(), formId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
