package com.jobnote.domain.document.service;

import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentVersion;
import com.jobnote.domain.document.dto.DocumentRequest;
import com.jobnote.domain.document.repository.DocumentRepository;
import com.jobnote.domain.document.repository.DocumentVersionRepository;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final UserService userService;

    @Transactional
    public Long uploadNewDocument(Long userId, DocumentRequest request) {
        User user = userService.getUserById(userId);
        Document document = request.toEntity(user);
        documentRepository.save(document);

        DocumentVersion version = DocumentVersion.builder()
                .version(1)
                .fileName(request.fileName())
                .fileUrl(request.fileUrl())
                .fileSize(request.fileSize())
                .document(document)
                .build();

        documentVersionRepository.save(version);

        return document.getId();
    }
}
