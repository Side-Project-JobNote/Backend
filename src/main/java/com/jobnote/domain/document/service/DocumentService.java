package com.jobnote.domain.document.service;

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
import com.jobnote.s3.service.S3Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_DOCUMENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final UserService userService;
    private final S3Service s3Service;

    @PersistenceContext
    private final EntityManager entityManager;

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

    public List<DocumentResponse> getAll(final Long userId) {
        return documentRepository.findAllByUserId(userId).stream()
                .map(DocumentResponse::from).toList();
    }

    public List<DocumentVersionResponse> getAllVersions(final Long userId, final Long documentId) {
        return documentVersionRepository.findAllByUserIdAndDocumentId(userId, documentId).stream()
                .map(docVer ->
                        DocumentVersionResponse.of(docVer, s3Service.generateFileUrl(docVer.getFileKey())))
                .toList();
    }

    @Transactional
    public void deleteDocument(final Long userId, final Long documentId) {
        Document document = getByIdOrThrow(documentId);
        document.validateOwner(userId);

        // s3에서 삭제
        List<DocumentVersion> allByDocumentId = documentVersionRepository.findAllByUserIdAndDocumentId(userId, document.getId());
        for (DocumentVersion documentVersion : allByDocumentId) {
            s3Service.deleteFile(documentVersion.getFileKey());
        }

        // 엔티티 삭제
        documentVersionRepository.deleteAll(allByDocumentId);
        entityManager.flush();
        documentRepository.delete(document);
    }

    @Transactional
    public void deleteAllDocuments(final Long userId) {
        List<Document> documents = documentRepository.findAllByUserId(userId);
        for (Document document : documents) {
            deleteDocument(userId, document.getId());
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
                .originFileName(request.fileName())
                .fileKey(request.fileKey())
                .fileSize(fileSize)
                .document(document)
                .build();

        return documentVersionRepository.save(documentVersion).getId();
    }
}
