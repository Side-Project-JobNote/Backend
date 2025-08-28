package com.jobnote.domain.schedule.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.global.annotation.swagger.PageableAsQueryParam;
import com.jobnote.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Tag(name = "Schedule", description = "일정 API")
public interface UserScheduleApi {

    @Operation(summary = "일정 목록 조회")
    @PageableAsQueryParam
    ResponseEntity<ApiResponse<Page<ScheduleResponse>>> getAllSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDate,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal,
            @Parameter(hidden = true) Pageable pageable
    );
}
