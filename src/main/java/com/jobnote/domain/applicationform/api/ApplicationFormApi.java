package com.jobnote.domain.applicationform.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.domain.applicationform.dto.ApplicationFormResponse;
import com.jobnote.global.annotation.swagger.ApiErrorResponseExplanation;
import com.jobnote.global.annotation.swagger.ApiResponseExplanations;
import com.jobnote.global.annotation.swagger.ApiSuccessResponseExplanation;
import com.jobnote.global.annotation.swagger.PageableAsQueryParam;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "ApplicationForm", description = "지원서 API")
public interface ApplicationFormApi {

    @Operation(summary = "지원서 생성")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseCode = ResponseCode.CREATED,
                    description = "생성 성공"
            )
    )
    ResponseEntity<ApiResponse<Void>> createApplicationForm(
            @RequestBody @Valid final ApplicationFormRequest request,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );

    @Operation(summary = "지원서 단건 조회")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseClass = ApplicationFormResponse.class,
                    description = "조회 성공"
            ),
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_APPLICATION_FORM),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<ApplicationFormResponse>> getApplicationForm(
            @PathVariable("id") final Long formId,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );

    @Operation(summary = "지원서 목록 조회")
    @PageableAsQueryParam
    ResponseEntity<ApiResponse<Page<ApplicationFormResponse>>> getAllApplicationForms(
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(summary = "지원서 업데이트",
            description = "일정 : (삭제)-> 삭제할 id를 요청에 미기재, (업데이트)-> id 기재 후 내용변경, (신규)-> id 미기재 내용 생성\n" +
                    "문서 : (삭제)-> 삭제할 id를 요청에 미기재, (신규)->id 미기재 documentId 기재")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseClass = ApplicationFormResponse.class,
                    description = "지원서 업데이트 성공"
            ),
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.INVALID_SCHEDULE_FORM_ASSOCIATION),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> updateApplicationForm(
            @PathVariable("id") final Long formId,
            @Valid @RequestBody final ApplicationFormRequest request,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );

    @Operation(summary = "지원서 삭제")
    @ApiResponseExplanations(
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_APPLICATION_FORM),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> deleteApplicationForm(
            @PathVariable("id") final Long formId,
            @Parameter(hidden = true) @LoginUser final CustomUserDetails principal
    );
}
