package com.jobnote.domain.document.controller;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomUserDetails;
import com.jobnote.domain.document.api.DocumentApi;
import com.jobnote.domain.document.dto.*;
import com.jobnote.domain.document.service.DocumentService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;

    /* CREATE */
    @Override
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Void>> uploadNewDocument(
            @RequestBody @Valid final DocumentRequest request,
            @LoginUser final CustomUserDetails principal
    ) {
        Long savedId = documentService.uploadNewDocument(principal.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    @Override
    @PostMapping("/upload/{documentId}")
    public ResponseEntity<ApiResponse<Void>> uploadNewVersionDocument(
            @PathVariable final Long documentId,
            @RequestBody @Valid final DocumentRequest request,
            @LoginUser final CustomUserDetails principal
    ) {
        Long savedId = documentService.uploadNewVersionDocument(principal.getUserId(), documentId, request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    /* READ */
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<Page<DocumentResponse>>> getAllDocuments(
            @LoginUser final CustomUserDetails principal,
            Pageable pageable
    ) {
        Page<DocumentResponse> documents = documentService.getAll(principal.getUserId(), pageable);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, documents));
    }

    @Override
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Page<DocumentVersionResponse>>> getAllDocumentVersions(
            @PathVariable final Long documentId,
            @LoginUser final CustomUserDetails principal,
            Pageable pageable
    ) {
        Page<DocumentVersionResponse> documents = documentService.getAllVersions(principal.getUserId(), documentId, pageable);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, documents));
    }

    /* DELETE */
    @Override
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable final Long documentId,
            @LoginUser final CustomUserDetails principal
    ) {
        documentService.delete(principal.getUserId(), documentId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
