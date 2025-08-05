package com.jobnote.domain.document.controller;

import com.jobnote.auth.config.LoginUser;
import com.jobnote.auth.dto.CustomPrincipal;
import com.jobnote.domain.document.api.DocumentApi;
import com.jobnote.domain.document.dto.DocumentRequest;
import com.jobnote.domain.document.dto.DocumentResponse;
import com.jobnote.domain.document.dto.DocumentVersionResponse;
import com.jobnote.domain.document.service.DocumentService;
import com.jobnote.global.common.ApiResponse;
import com.jobnote.global.common.ResponseCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController implements DocumentApi {

    private final DocumentService documentService;

    /* CREATE */
    @Override
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<Void>> uploadNewDocument(
            @RequestBody final DocumentRequest request,
            @LoginUser final CustomPrincipal principal
    ) {
        Long savedId = documentService.uploadNewDocument(principal.getUserId(), request);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(savedId).toUri();

        return ResponseEntity.created(location).body(ApiResponse.ofSuccess(ResponseCode.CREATED));
    }

    @Override
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

    /* READ */
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<DocumentResponse>>> getAllDocuments(
            @LoginUser final CustomPrincipal principal
    ) {
        List<DocumentResponse> documents = documentService.getAll(principal.getUserId());

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, documents));
    }

    @Override
    @GetMapping("/{documentId}")
    public ResponseEntity<ApiResponse<List<DocumentVersionResponse>>> getAllDocumentVersions(
            @PathVariable final Long documentId,
            @LoginUser final CustomPrincipal principal
    ) {
        List<DocumentVersionResponse> documents = documentService.getAllVersions(documentId, principal.getUserId());

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK, documents));
    }

    /* DELETE */
    @Override
    @DeleteMapping("/{documentId}")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(
            @PathVariable final Long documentId,
            @LoginUser final CustomPrincipal principal
    ) {
        documentService.deleteDocument(principal.getUserId(), documentId);

        return ResponseEntity.ok(ApiResponse.ofSuccess(ResponseCode.OK));
    }
}
