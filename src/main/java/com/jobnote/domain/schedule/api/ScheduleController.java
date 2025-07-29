package com.jobnote.domain.schedule.api;

import com.jobnote.domain.applicationform.service.ApplicationFormService;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.service.ScheduleService;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;
    private final ApplicationFormService applicationFormService;

    /* CREATE */
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createSchedule(
            final Long formId,
            @RequestBody @Valid final ScheduleRequest request,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        Long savedScheduleId = scheduleService.save(userId, applicationFormService.getByIdOrThrow(formId), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedScheduleId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* READ */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScheduleResponse>> getSchedule(
            @PathVariable("id") final Long scheduleId,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        ScheduleResponse form = scheduleService.getById(userId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, form));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ScheduleResponse>>> getAllSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDate,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        List<ScheduleResponse> schedules = scheduleService.getAll(userId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, schedules));
    }

    /* UPDATE */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> updateSchedule(
            @PathVariable("id") final Long scheduleId,
            @Valid @RequestBody final ScheduleRequest request,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        scheduleService.update(userId, scheduleId, request);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }

    /* DELETE */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable("id") final Long scheduleId,
            @AuthenticationPrincipal final UserDetails user
    ) {
        Long userId = userService.getUserIdFromUserDetails(user);
        scheduleService.delete(userId, scheduleId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
