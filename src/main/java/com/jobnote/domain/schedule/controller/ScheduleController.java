package com.jobnote.domain.schedule.controller;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.schedule.api.ScheduleApi;
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
public class ScheduleController implements ScheduleApi {

    private final ScheduleService scheduleService;

    /* CREATE */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSchedule(
            @PathVariable Long formId,
            @RequestBody @Valid final ScheduleRequest request,
            @LoginUser final CustomUserDetails principal
    ) {
        Long savedScheduleId = scheduleService.save(principal.getUserId(), formId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedScheduleId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* READ */
    @Override
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @LoginUser final CustomUserDetails principal
    ) {
        ScheduleResponse form = scheduleService.getById(principal.getUserId(), formId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, form));
    }

    /* UPDATE */
    @Override
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @Valid @RequestBody final ScheduleRequest request,
            @LoginUser final CustomUserDetails principal
    ) {
        scheduleService.update(principal.getUserId(), formId, scheduleId, request);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* DELETE */
    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @LoginUser final CustomUserDetails principal
    ) {
        scheduleService.delete(principal.getUserId(), formId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
