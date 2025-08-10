package com.jobnote.domain.document.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.domain.document.dto.*;
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

@Tag(name = "Document", description = "문서 API")
public interface DocumentApi {

    @Operation(summary = "문서 업로드(신규 문서 등록)",
               description = "NOT_FOUND_S3_FILE : PresignedUrl을 통해 등록된 파일이 아닌 경우<br>" +
                             "FORBIDDEN : 다른 유저의 파일에 접근한 경우")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseCode = ResponseCode.CREATED,
                    description = "생성 성공"
            ),
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_S3_FILE),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> uploadNewDocument(
            @RequestBody @Valid final DocumentRequest request,
            @Parameter(hidden = true) @LoginUser final CustomPrincipal principal
    );

    @Operation(summary = "기존 문서의 새로운 버전 문서 업로드",
               description = "NOT_FOUND_DOCUMENT : 존재하지 않는 문서에 새로운 버전을 등록하려고 하는 경우<br>" +
                             "NOT_FOUND_S3_FILE : PresignedUrl을 통해 등록된 파일이 아닌 경우<br>" +
                             "FORBIDDEN : 다른 유저의 파일에 접근한 경우")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseCode = ResponseCode.CREATED,
                    description = "생성 성공"
            ),
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_DOCUMENT),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> uploadNewVersionDocument(
            @PathVariable final Long documentId,
            @RequestBody @Valid final DocumentRequest request,
            @Parameter(hidden = true) @LoginUser final CustomPrincipal principal
    );

    @Operation(summary = "문서 목록 조회")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseClass = DocumentListResponse.class,
                    description = "조회 성공"
            )
    )
    ResponseEntity<ApiResponse<DocumentListResponse>> getAllDocuments(
            @Parameter(hidden = true) @LoginUser final CustomPrincipal principal
    );

    @Operation(summary = "문서의 모든 버전 목록 조회")
    @ApiResponseExplanations(
            success = @ApiSuccessResponseExplanation(
                    responseClass = DocumentVersionListResponse.class,
                    description = "조회 성공"
            )
    )
    ResponseEntity<ApiResponse<DocumentVersionListResponse>> getAllDocumentVersions(
            @PathVariable final Long documentId,
            @Parameter(hidden = true) @LoginUser final CustomPrincipal principal
    );

    @Operation(summary = "문서 삭제")
    @ApiResponseExplanations(
            errors = {
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.NOT_FOUND_DOCUMENT),
                    @ApiErrorResponseExplanation(exceptionCode = ResponseCode.FORBIDDEN)
            }
    )
    ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable final Long documentId,
            @Parameter(hidden = true) @LoginUser final CustomPrincipal principal
    );
}
