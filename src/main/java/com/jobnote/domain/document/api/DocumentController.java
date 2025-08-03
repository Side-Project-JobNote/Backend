package com.jobnote.domain.document.api;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.domain.document.dto.DocumentRequest;
import com.jobnote.domain.document.service.DocumentService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/document")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Void>> uploadNewDocument(
            @RequestBody final DocumentRequest request,
            @LoginUser final CustomPrincipal principal
    ) {
        Long savedId = documentService.uploadNewDocument(principal.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    @PostMapping("/upload/{documentId}")
    public ResponseEntity<ApiResponse<Void>> uploadNewVersionDocument(
            @PathVariable final Long documentId,
            @RequestBody final DocumentRequest request,
            @LoginUser final CustomPrincipal principal
    ) {
        Long savedId = documentService.uploadNewVersionDocument(principal.getUserId(), documentId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }
}
