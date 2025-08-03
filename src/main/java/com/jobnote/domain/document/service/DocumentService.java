package com.jobnote.domain.document.service;

import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentVersion;
import com.jobnote.domain.document.dto.DocumentRequest;
import com.jobnote.domain.document.repository.DocumentRepository;
import com.jobnote.domain.document.repository.DocumentVersionRepository;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
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

    @Transactional
    public Long uploadNewDocument(final Long userId, final DocumentRequest request) {
        User user = userService.getUserById(userId);
        Document document = request.toEntity(user);
        documentRepository.save(document);

        DocumentVersion documentVersion = DocumentVersion.builder()
                .version(1)
                .fileName(request.fileName())
                .fileUrl(request.fileUrl())
                .fileSize(request.fileSize())
                .document(document)
                .build();

        documentVersionRepository.save(documentVersion);

        return document.getId();
    }

    @Transactional
    public Long uploadNewVersionDocument(final Long userId, final Long documentId, final DocumentRequest request) {
        Document document = getByIdOrThrow(documentId);
        document.validateOwner(userId);

        int version = documentVersionRepository.findMaxVersionByDocumentId(documentId).orElse(1);
        DocumentVersion documentVersion = DocumentVersion.builder()
                .version(version + 1)
                .fileName(request.fileName())
                .fileUrl(request.fileUrl())
                .fileSize(request.fileSize())
                .document(document)
                .build();

        DocumentVersion savedDocumentVersion = documentVersionRepository.save(documentVersion);

        return savedDocumentVersion.getId();
    }

    /* HELPER METHOD */
    public Document getByIdOrThrow(final Long docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_DOCUMENT));
    }
}
