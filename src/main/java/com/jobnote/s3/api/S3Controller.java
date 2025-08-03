package com.jobnote.s3.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import com.jobnote.s3.dto.PresignedFileRequest;
import com.jobnote.s3.dto.PresignedFileResponse;
import com.jobnote.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/s3")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/presigned")
    public ResponseEntity<ApiResponse<PresignedFileResponse>> getPresignedUrl(
            @RequestBody final PresignedFileRequest request,
            @LoginUser final CustomPrincipal principal
    ) {
        PresignedFileResponse signedUrl = s3Service.generatePresignedFile(principal.getUserId(), request);
        return  ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, signedUrl));
    }
}
