package com.jobnote.domain.schedule.controller;

import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.service.ScheduleService;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class UserScheduleController {

    private final ScheduleService scheduleService;
    private final UserService userService;

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
}
