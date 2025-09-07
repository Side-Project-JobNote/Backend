package com.jobnote.domain.applicationformdocument.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.dto.ApplicationFormSimpleResponse;
import com.jobnote.domain.applicationformdocument.domain.ApplicationFormDocument;
import com.jobnote.domain.applicationformdocument.dto.ApplicationFormDocumentRequest;
import com.jobnote.domain.applicationformdocument.repository.ApplicationFormDocumentRepository;
import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.dto.DocumentSimpleResponse;
import com.jobnote.domain.document.repository.DocumentRepository;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.APPLIED;
import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.PLANNED;
import static com.jobnote.domain.document.domain.DocumentType.RESUME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

class ApplicationFormDocumentServiceTest extends ServiceUnitTest {

    @InjectMocks private ApplicationFormDocumentService applicationFormDocumentService;

    @Mock private ApplicationFormDocumentRepository applicationFormDocumentRepository;
    @Mock private DocumentRepository documentRepository;

    private ApplicationForm applicationForm;
    private ApplicationForm applicationForm2;
    private Document document;
    private Document document2;

    private final Long userId = 1L;
    private final Long formId = 1L;
    private final Long formId2 = 2L;
    private final Long documentId = 1L;
    private final Long documentId2 = 2L;

    @BeforeEach
    void setUp() {
        User user = UserFixture.createMember(userId, "test@gmail.com", "1234", "test");
        applicationForm = ApplicationForm.builder()
                .user(user)
                .companyName("네이버")
                .companyAddress("경기도 성남시 분당구")
                .companyUrl("https://naver.com")
                .companyScale("대기업")
                .position("백엔드 개발자")
                .status(APPLIED)
                .build();
        applicationForm2 = ApplicationForm.builder()
                .user(user)
                .companyName("카카오")
                .companyAddress("경기도 성남시 분당구")
                .companyUrl("https://kakao.com")
                .companyScale("대기업")
                .position("프론트엔드 개발자")
                .status(PLANNED)
                .build();
        document = Document.builder()
                .user(user)
                .documentType(RESUME)
                .title("네이버 이력서")
                .build();
        document2 = Document.builder()
                .user(user)
                .documentType(RESUME)
                .title("카카오 이력서")
                .build();
        ReflectionTestUtils.setField(applicationForm, "id", formId);
        ReflectionTestUtils.setField(applicationForm2, "id", formId2);
        ReflectionTestUtils.setField(document, "id", documentId);
        ReflectionTestUtils.setField(document2, "id", documentId2);
    }

    @DisplayName("지원서-문서 연결 등록 및 조회")
    @Test
    void connectDocumentToApplicationForm() {
        // given
        List<ApplicationFormDocument> afds = List.of(
                ApplicationFormDocument.builder().applicationForm(applicationForm).document(document).build(),
                ApplicationFormDocument.builder().applicationForm(applicationForm).document(document2).build()
        );

        given(documentRepository.findById(1L)).willReturn(Optional.of(document));
        given(documentRepository.findById(2L)).willReturn(Optional.of(document2));
        given(applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(eq(userId), eq(List.of(formId)))).willReturn(afds);

        // when
        List<ApplicationFormDocumentRequest> requests = List.of(
                new ApplicationFormDocumentRequest(1L),
                new ApplicationFormDocumentRequest(2L)
        );
        applicationFormDocumentService.saveAll(userId, applicationForm, requests);

        // then
        List<ApplicationFormDocument> actualResult = applicationFormDocumentService.getAllByApplicationFormId(userId, formId);
        assertThat(actualResult).hasSize(2);
        assertThat(actualResult.get(0).getDocument().getTitle()).isEqualTo("네이버 이력서");
    }

    @DisplayName("문서 기준으로 연결된 지원서 목록 조회")
    @Test
    void getAllSimpleByDocumentId() {
        ApplicationFormDocument afd = ApplicationFormDocument.builder().applicationForm(applicationForm).document(document).build();
        given(applicationFormDocumentRepository.findAllByUserIdAndDocumentId(userId, documentId)).willReturn(List.of(afd));

        List<ApplicationFormSimpleResponse> actualResult = applicationFormDocumentService.getAllSimpleByDocumentId(userId, documentId);

        assertThat(actualResult).hasSize(1);
        assertThat(actualResult.get(0).companyName()).isEqualTo("네이버");
    }

    @DisplayName("지원서 기준으로 연결된 문서 목록 조회")
    @Test
    void getAllSimpleByApplicationFormId() {
        ApplicationFormDocument afd = ApplicationFormDocument.builder().applicationForm(applicationForm).document(document).build();
        ApplicationFormDocument afd2 = ApplicationFormDocument.builder().applicationForm(applicationForm).document(document2).build();
        given(applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, List.of(formId))).willReturn(List.of(afd, afd2));

        List<DocumentSimpleResponse> actualResult = applicationFormDocumentService.getAllSimpleByApplicationFormId(userId, formId);

        assertThat(actualResult).hasSize(2);
        assertThat(actualResult.get(0).title()).isEqualTo("네이버 이력서");
        assertThat(actualResult.get(1).title()).isEqualTo("카카오 이력서");
    }

    @DisplayName("지원서 ID별 문서 목록 그룹핑 조회")
    @Test
    void getAllSimpleGroupedByApplicationFormIds() {
        ApplicationFormDocument afd = ApplicationFormDocument.builder().applicationForm(applicationForm).document(document).build();
        ApplicationFormDocument afd2 = ApplicationFormDocument.builder().applicationForm(applicationForm2).document(document2).build();
        given(applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, List.of(formId, formId2))).willReturn(List.of(afd, afd2));

        Map<Long, List<DocumentSimpleResponse>> actualResult = applicationFormDocumentService.getAllSimpleGroupedByApplicationFormIds(userId, List.of(formId, formId2));

        assertThat(actualResult.get(formId)).hasSize(1);
        assertThat(actualResult.get(formId).get(0).title()).isEqualTo("네이버 이력서");
        assertThat(actualResult.get(formId2)).hasSize(1);
        assertThat(actualResult.get(formId2).get(0).title()).isEqualTo("카카오 이력서");
    }
}
