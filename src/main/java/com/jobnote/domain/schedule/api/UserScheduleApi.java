package com.jobnote.domain.schedule.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.schedule.dto.ScheduleListResponse;
import com.jobnote.global.annotation.swagger.ApiResponseExplanations;
import com.jobnote.global.annotation.swagger.ApiSuccessResponseExplanation;
import com.jobnote.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;

@Tag(name = "Schedule", description = "일정 API")
public interface UserScheduleApi {

    @Operation(summary = "일정 목록 조회")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseClass = ScheduleListResponse.class,
                    description = "조회 성공"
            )
    )
    ResponseEntity<ApiResponse<ScheduleListResponse>> getAllSchedules(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDate,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );
}
