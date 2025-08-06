package com.jobnote.s3.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.global.annotation.swagger.ApiResponseExplanations;
import com.jobnote.global.annotation.swagger.ApiSuccessResponseExplanation;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.s3.dto.PresignedFileRequest;
import com.jobnote.s3.dto.PresignedFileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "S3", description = "AWS S3 파일 API")
public interface S3Api {

    @Operation(summary = "PresignedUrl 획득")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseClass = PresignedFileResponse.class,
                    description = "요청 성공"
            )
    )
    ResponseEntity<ApiResponse<PresignedFileResponse>> getPresignedUrl(
            @RequestBody @Valid final PresignedFileRequest request,
            @Parameter(hidden = true) @LoginUser final CustomPrincipal principal
    );
}
