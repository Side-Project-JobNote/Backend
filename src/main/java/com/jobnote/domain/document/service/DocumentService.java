package com.jobnote.domain.document.service;

import com.jobnote.domain.applicationform.service.ApplicationFormService;
import com.jobnote.domain.applicationformdocument.service.ApplicationFormDocumentService;
import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentVersion;
import com.jobnote.domain.document.dto.DocumentRequest;
import com.jobnote.domain.document.dto.DocumentResponse;
import com.jobnote.domain.document.dto.DocumentVersionResponse;
import com.jobnote.domain.document.repository.DocumentRepository;
import com.jobnote.domain.document.repository.DocumentVersionRepository;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.infra.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_DOCUMENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final UserService userService;
    private final S3Service s3Service;
    private final ApplicationFormService applicationFormService;
    private final ApplicationFormDocumentService applicationFormDocumentService;

    @Transactional
    public Long uploadNewDocument(final Long userId, final DocumentRequest request) {
        User user = userService.getUserById(userId);
        Document document = request.toEntity(user);
        documentRepository.save(document);

        return saveDocumentVersion(userId, document, request, 1);
    }

    @Transactional
    public Long uploadNewVersionDocument(final Long userId, final Long documentId, final DocumentRequest request) {
        Document document = getByIdOrThrow(documentId);
        document.validateOwner(userId);

        int version = documentVersionRepository.findMaxVersionByDocumentId(documentId).orElse(0) + 1;

        return saveDocumentVersion(userId, document, request, version);
    }

    public Page<DocumentResponse> getAll(final Long userId, final Pageable pageable) {
        return documentRepository.findAllByUserId(userId, pageable)
                .map(document -> DocumentResponse.from(document, applicationFormService.getAllSimple(userId)));
    }

    public Page<DocumentVersionResponse> getAllVersions(final Long userId, final Long documentId, final Pageable pageable) {
        return documentVersionRepository.findAllByUserIdAndDocumentId(userId, documentId, pageable)
                .map(docVer ->
                        DocumentVersionResponse.of(docVer, s3Service.generateFileUrl(docVer.getFileKey())));
    }

    @Transactional
    public void delete(final Long userId, final Long documentId) {
        Document document = getByIdOrThrow(documentId);
        document.validateOwner(userId);

        // s3에서 삭제
        Page<DocumentVersion> allByDocumentId = documentVersionRepository.findAllByUserIdAndDocumentId(userId, documentId, Pageable.unpaged());
        for (DocumentVersion documentVersion : allByDocumentId) {
            s3Service.deleteFile(documentVersion.getFileKey());
        }

        // 엔티티 삭제
        documentVersionRepository.deleteAllByDocumentId(documentId);
        applicationFormDocumentService.deleteAllByDocumentId(documentId);
        documentRepository.delete(document);
    }

    @Transactional
    public void deleteAllDocuments(final Long userId) {
        Page<Document> documents = documentRepository.findAllByUserId(userId, Pageable.unpaged());
        for (Document document : documents) {
            delete(userId, document.getId());
        }
    }

    /* HELPER METHOD */
    private Document getByIdOrThrow(final Long docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_DOCUMENT));
    }

    private Long saveDocumentVersion(final Long userId, final Document document, final DocumentRequest request, final int version) {
        s3Service.validateIsOwner(userId, request.fileKey());
        long fileSize = s3Service.getFileSize(request.fileKey());

        DocumentVersion documentVersion = DocumentVersion.builder()
                .version(version)
                .title(request.title())
                .originFileName(request.fileName())
                .fileKey(request.fileKey())
                .fileSize(fileSize)
                .document(document)
                .build();

        return documentVersionRepository.save(documentVersion).getId();
    }
}
