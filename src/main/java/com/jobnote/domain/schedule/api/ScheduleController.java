package com.jobnote.domain.schedule.api;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.service.ApplicationFormService;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.service.ScheduleService;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/application-forms/{formId}/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final ApplicationFormService applicationFormService;

    /* CREATE */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSchedule(
            @PathVariable Long formId,
            @RequestBody @Valid final ScheduleRequest request,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        ApplicationForm form = applicationFormService.getByIdOrThrow(formId);

        Long savedScheduleId = scheduleService.save(userId, form, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedScheduleId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* READ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        ScheduleResponse form = scheduleService.getById(userId, formId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, form));
    }

    /* UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @Valid @RequestBody final ScheduleRequest request,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        scheduleService.update(userId, formId, scheduleId, request);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        scheduleService.delete(userId, formId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
