package com.jobnote.domain.document.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.applicationform.dto.ApplicationFormSimpleResponse;
import com.jobnote.domain.applicationformdocument.service.ApplicationFormDocumentService;
import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.domain.DocumentType;
import com.jobnote.domain.document.domain.DocumentVersion;
import com.jobnote.domain.document.dto.DocumentRequest;
import com.jobnote.domain.document.dto.DocumentResponse;
import com.jobnote.domain.document.dto.DocumentVersionResponse;
import com.jobnote.domain.document.repository.DocumentRepository;
import com.jobnote.domain.document.repository.DocumentVersionRepository;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserFixture;
import com.jobnote.domain.user.service.UserService;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.infra.s3.service.S3Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.APPLIED;
import static com.jobnote.domain.document.domain.DocumentType.RESUME;
import static com.jobnote.global.common.ResponseCode.FORBIDDEN;
import static com.jobnote.global.common.ResponseCode.NOT_FOUND_DOCUMENT;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

class DocumentServiceTest extends ServiceUnitTest {

    @InjectMocks private DocumentService documentService;

    @Mock private DocumentRepository documentRepository;
    @Mock private DocumentVersionRepository documentVersionRepository;
    @Mock private UserService userService;
    @Mock private S3Service s3Service;
    @Mock private ApplicationFormDocumentService applicationFormDocumentService;

    private User user;
    private Document document;
    private DocumentVersion documentVersion;

    private final Long userId = 1L;
    private final Long documentId = 1L;

    @BeforeEach
    void setUp() {
        user = UserFixture.createMember(userId, "test@gmail.com", "1234", "test");

        document = Document.builder()
                .user(user)
                .documentType(RESUME)
                .title("네이버 이력서")
                .build();
        ReflectionTestUtils.setField(document, "id", documentId);
        ReflectionTestUtils.setField(document, "modifiedDate", LocalDateTime.now());

        documentVersion = DocumentVersion.builder()
                .document(document)
                .version(1)
                .title("네이버 이력서")
                .originFileName("naver_resume.pdf")
                .fileKey("1/naver_resume.pdf")
                .fileSize(1024L)
                .build();
        ReflectionTestUtils.setField(documentVersion, "id", 10L);
        ReflectionTestUtils.setField(documentVersion, "createdDate", LocalDateTime.now());
    }

    @DisplayName("새로운 문서 업로드")
    @Test
    void uploadNewDocument() {
        DocumentRequest request = new DocumentRequest(
                "네이버 이력서",
                "naver_resume.pdf",
                "1/naver_resume.pdf",
                RESUME,
                1024L
        );

        given(userService.getUserById(userId)).willReturn(user);
        given(documentRepository.save(any(Document.class))).willReturn(document);
        willDoNothing().given(s3Service).validateIsOwner(userId, request.fileKey());
        given(s3Service.getFileSize(request.fileKey())).willReturn(1024L);
        given(documentVersionRepository.save(any(DocumentVersion.class))).willReturn(documentVersion);

        documentService.uploadNewDocument(userId, request);

        then(documentRepository).should().save(any(Document.class));
        then(documentVersionRepository).should().save(any(DocumentVersion.class));
    }

    @DisplayName("존재하지 않는 문서 ID로 버전 업로드 시 예외 발생")
    @Test
    void uploadNewVersionDocument_notFound() {
        DocumentRequest request = new DocumentRequest(
                "카카오 이력서",
                "kakao_resume.pdf",
                "1/kakao_resume.pdf",
                RESUME,
                2048L
        );
        given(documentRepository.findById(documentId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> documentService.uploadNewVersionDocument(userId, documentId, request))
                .isInstanceOf(JobNoteException.class)
                .hasMessageContaining(NOT_FOUND_DOCUMENT.getMessage());
    }

    @DisplayName("문서 전체 조회시 최신 버전과 연결된 지원서 정보를 포함한다")
    @Test
    void getAll() {
        Page<Document> page = new PageImpl<>(List.of(document));

        given(documentRepository.findAllByUserId(eq(userId), any(Pageable.class))).willReturn(page);
        given(documentVersionRepository.findLatestVersionByDocumentId(documentId)).willReturn(1);
        given(applicationFormDocumentService.getAllSimpleByDocumentId(userId, documentId))
                .willReturn(List.of(new ApplicationFormSimpleResponse(document.getId(), "네이버", "백엔드", APPLIED)));

        Page<DocumentResponse> actual = documentService.getAll(userId, Pageable.unpaged());

        assertThat(actual.getContent()).hasSize(1);
        assertThat(actual.getContent().get(0).title()).isEqualTo("네이버 이력서");
    }

    @DisplayName("문서 버전 전체 조회 시 S3 파일 URL을 포함한다")
    @Test
    void getAllVersions() {
        Page<DocumentVersion> page = new PageImpl<>(List.of(documentVersion));

        given(documentVersionRepository.findAllByUserIdAndDocumentId(eq(userId), eq(documentId), any(Pageable.class)))
                .willReturn(page);
        given(s3Service.generateFileUrl(documentVersion.getFileKey())).willReturn("https://s3.com/1/naver_resume.pdf");

        Page<DocumentVersionResponse> actual = documentService.getAllVersions(userId, documentId, Pageable.unpaged());

        assertThat(actual.getContent()).hasSize(1);
        assertThat(actual.getContent().get(0).fileUrl()).isEqualTo("https://s3.com/1/naver_resume.pdf");
    }

    @DisplayName("문서를 삭제하면 S3, 매핑 엔티티, 버전, 문서 순으로 삭제된다")
    @Test
    void delete() {
        given(documentRepository.findById(documentId)).willReturn(Optional.of(document));
        given(documentVersionRepository.findAllByUserIdAndDocumentId(eq(userId), eq(documentId), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(documentVersion)));

        willDoNothing().given(s3Service).deleteFile(documentVersion.getFileKey());
        willDoNothing().given(applicationFormDocumentService).deleteAllByDocumentId(documentId);
        willDoNothing().given(documentVersionRepository).deleteAllByDocumentId(documentId);
        willDoNothing().given(documentRepository).delete(document);

        documentService.delete(userId, documentId);

        then(s3Service).should().deleteFile(documentVersion.getFileKey());
        then(applicationFormDocumentService).should().deleteAllByDocumentId(documentId);
        then(documentVersionRepository).should().deleteAllByDocumentId(documentId);
        then(documentRepository).should().delete(document);
    }

    @DisplayName("문서 삭제 시 소유자가 아니면 예외 발생")
    @Test
    void delete_forbidden() {
        User otherUser = UserFixture.createMember(2L, "other@gmail.com", "1234", "other");
        Document otherDoc = Document.builder().user(otherUser).documentType(DocumentType.RESUME).title("타인 문서").build();
        ReflectionTestUtils.setField(otherDoc, "id", 5L);

        given(documentRepository.findById(5L)).willReturn(Optional.of(otherDoc));

        assertThatThrownBy(() -> documentService.delete(userId, 5L))
                .isInstanceOf(JobNoteException.class)
                .hasMessageContaining(FORBIDDEN.getMessage());
    }
}
