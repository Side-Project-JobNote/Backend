package com.jobnote.domain.schedule.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.global.annotation.swagger.ApiErrorResponseExplanation;
import com.jobnote.global.annotation.swagger.ApiResponseExplanations;
import com.jobnote.global.annotation.swagger.ApiSuccessResponseExplanation;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Schedule", description = "일정 API")
public interface ScheduleApi {

    @Operation(summary = "일정 생성")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseCode = ResponseCode.CREATED,
                    description = "생성 성공"
            ),
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_APPLICATION_FORM),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> createSchedule(
            @PathVariable Long formId,
            @RequestBody @Valid final ScheduleRequest request,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );

    @Operation(summary = "일정 단건 조회")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseClass = ScheduleResponse.class,
                    description = "조회 성공"
            ),
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_SCHEDULE),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.INVALID_SCHEDULE_FORM_ASSOCIATION),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<ScheduleResponse>> getSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );

    @Operation(summary = "일정 업데이트")
    @ApiResponseExplanations(
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_SCHEDULE),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.INVALID_SCHEDULE_FORM_ASSOCIATION),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> updateSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @Valid @RequestBody final ScheduleRequest request,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );

    @Operation(summary = "일정 삭제")
    @ApiResponseExplanations(
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_SCHEDULE),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.INVALID_SCHEDULE_FORM_ASSOCIATION),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> deleteSchedule(
            @PathVariable Long formId,
            @PathVariable("id") final Long scheduleId,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );
}
