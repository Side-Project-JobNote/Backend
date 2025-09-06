package com.jobnote.domain.applicationform.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationformdocument.repository.ApplicationFormDocumentRepository;
import com.jobnote.domain.applicationform.repository.ApplicationFormRepository;
import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.domain.applicationform.dto.ApplicationFormResponse;
import com.jobnote.domain.applicationformdocument.service.ApplicationFormDocumentService;
import com.jobnote.domain.document.domain.DocumentType;
import com.jobnote.domain.document.dto.DocumentSimpleResponse;
import com.jobnote.domain.document.service.DocumentService;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.service.ScheduleService;
import com.jobnote.domain.user.domain.UserFixture;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jobnote.domain.schedule.domain.ScheduleStatus.PLANNED;
import static com.jobnote.global.common.ResponseCode.*;
import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.APPLIED;
import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.DOCUMENT_PASSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

class ApplicationFormServiceTest extends ServiceUnitTest {

    @InjectMocks
    private ApplicationFormService applicationFormService;

    @Mock
    private UserService userService;

    @Mock
    private ScheduleService scheduleService;

    @Mock
    private DocumentService documentService;

    @Mock
    private ApplicationFormDocumentService applicationFormDocumentService;

    @Mock
    private ApplicationFormRepository applicationFormRepository;

    @Mock
    private ApplicationFormDocumentRepository applicationFormDocumentRepository;

    private User user;
    private ApplicationForm applicationForm;

    private final Long userId = 1L;
    private final Long formId = 1L;

    @BeforeEach
    void setUp() {
        user = UserFixture.createMember(userId, "test@gmail.com", "1234", "test");
        applicationForm = ApplicationForm.builder()
                .user(user)
                .companyName("네이버")
                .companyAddress("경기도 성남시 분당구")
                .companyUrl("https://naver.com")
                .companyScale("대기업")
                .position("백엔드 개발자")
                .status(APPLIED)
                .build();
    }

    @Nested
    @DisplayName("지원서 다건 조회")
    class GetAll {

        @DisplayName("지원서 목록을 전체 조회한다.")
        @Test
        void getAllApplicationForms() {
            // given
            ApplicationForm form1 = ApplicationForm.builder().user(user).companyName("네이버").status(APPLIED).build();
            ApplicationForm form2 = ApplicationForm.builder().user(user).companyName("카카오").status(DOCUMENT_PASSED).build();
            List<ApplicationForm> expectedResult = Arrays.asList(form1, form2);

            Page<ApplicationForm> pageExpectedResult = new PageImpl<>(expectedResult);
            given(applicationFormRepository.findAllByUserId(eq(userId), any(Pageable.class))).willReturn(pageExpectedResult);

            // when
            Page<ApplicationFormResponse> pageActualResult = applicationFormService.getAll(userId, Pageable.unpaged());
            List<ApplicationFormResponse> actualResult = pageActualResult.getContent();

            // then
            assertThat(actualResult).hasSize(2);
            assertThat(actualResult.get(0).companyName()).isEqualTo("네이버");
            then(applicationFormRepository).should().findAllByUserId(userId, Pageable.unpaged());
        }

        @Test
        @DisplayName("지원서마다 일정들이 그룹핑되어 함께 반환된다")
        void getAllApplicationForms_withSchedules() {
            // given
            ApplicationForm form1 = ApplicationForm.builder().user(user).companyName("네이버").status(APPLIED).build();
            ApplicationForm form2 = ApplicationForm.builder().user(user).companyName("카카오").status(DOCUMENT_PASSED).build();
            ReflectionTestUtils.setField(form1, "id", 1L);
            ReflectionTestUtils.setField(form2, "id", 2L);
            List<ApplicationForm> forms = List.of(form1, form2);

            Page<ApplicationForm> pageForms = new PageImpl<>(forms);
            given(applicationFormRepository.findAllByUserId(eq(userId), any(Pageable.class))).willReturn(pageForms);

            ScheduleResponse schedule1 = new ScheduleResponse(101L, "지원서 제출", "오전", LocalDateTime.of(2025, 8, 1, 10, 0), PLANNED);
            ScheduleResponse schedule2 = new ScheduleResponse(102L, "코딩테스트", "연습문제 풀이", LocalDateTime.of(2025, 8, 2, 9, 0), PLANNED);
            ScheduleResponse schedule3 = new ScheduleResponse(103L, "2차 면접", "예상질문지 복습", LocalDateTime.of(2025, 8, 5, 9, 0), PLANNED);

            given(scheduleService.getAllGroupedByApplicationFormIds(eq(userId), eq(List.of(1L, 2L))))
                    .willReturn(Map.of(
                            1L, List.of(schedule1),
                            2L, List.of(schedule2, schedule3)
                    ));

            // when
            Page<ApplicationFormResponse> actualResult = applicationFormService.getAll(userId, Pageable.unpaged());

            // then
            assertThat(actualResult).hasSize(2);

            ApplicationFormResponse result1 = actualResult.getContent().get(0);
            assertThat(result1.companyName()).isEqualTo("네이버");
            assertThat(result1.schedules()).hasSize(1);
            assertThat(result1.schedules().get(0).title()).isEqualTo("지원서 제출");

            ApplicationFormResponse result2 = actualResult.getContent().get(1);
            assertThat(result2.companyName()).isEqualTo("카카오");
            assertThat(result2.schedules()).hasSize(2);
            assertThat(result2.schedules().get(0).title()).isEqualTo("코딩테스트");

            then(applicationFormRepository).should().findAllByUserId(eq(userId), any(Pageable.class));
            then(scheduleService).should().getAllGroupedByApplicationFormIds(eq(userId), eq(List.of(1L, 2L)));
        }

        @Test
        @DisplayName("지원서마다 문서들이 그룹핑되어 함께 반환된다")
        void getAllApplicationForms_withDocuments() {
            // given
            ApplicationForm form1 = ApplicationForm.builder().user(user).companyName("네이버").status(APPLIED).build();
            ApplicationForm form2 = ApplicationForm.builder().user(user).companyName("카카오").status(DOCUMENT_PASSED).build();
            ReflectionTestUtils.setField(form1, "id", 1L);
            ReflectionTestUtils.setField(form2, "id", 2L);
            List<ApplicationForm> forms = List.of(form1, form2);

            Page<ApplicationForm> pageForms = new PageImpl<>(forms);
            given(applicationFormRepository.findAllByUserId(eq(userId), any(Pageable.class))).willReturn(pageForms);

            DocumentSimpleResponse document1 = new DocumentSimpleResponse(101L, DocumentType.RESUME, "네이버 이력서");
            DocumentSimpleResponse document2 = new DocumentSimpleResponse(102L, DocumentType.COVER_LETTER, "네이버 자소서");
            DocumentSimpleResponse document3 = new DocumentSimpleResponse(103L, DocumentType.RESUME, "카카오 이력서");

            given(applicationFormDocumentService.getAllSimpleGroupedByApplicationFormIds(eq(userId), eq(List.of(1L, 2L))))
                    .willReturn(Map.of(
                            1L, List.of(document1, document2),
                            2L, List.of(document3)
                    ));

            // when
            Page<ApplicationFormResponse> actualResult = applicationFormService.getAll(userId, Pageable.unpaged());

            // then
            assertThat(actualResult).hasSize(2);

            ApplicationFormResponse result1 = actualResult.getContent().get(0);
            assertThat(result1.companyName()).isEqualTo("네이버");
            assertThat(result1.documents()).hasSize(2);
            assertThat(result1.documents().get(0).title()).isEqualTo("네이버 이력서");

            ApplicationFormResponse result2 = actualResult.getContent().get(1);
            assertThat(result2.companyName()).isEqualTo("카카오");
            assertThat(result2.documents()).hasSize(1);
            assertThat(result2.documents().get(0).title()).isEqualTo("카카오 이력서");

            then(applicationFormRepository).should().findAllByUserId(eq(userId), any(Pageable.class));
            then(applicationFormDocumentService).should().getAllSimpleGroupedByApplicationFormIds(eq(userId), eq(List.of(1L, 2L)));
        }

    }

    @Nested
    @DisplayName("지원서 단건 조회")
    class GetById {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));

            // when
            applicationFormService.getById(userId, formId);

            // then
            then(applicationFormRepository).should().findById(formId);
        }

        @DisplayName("실패 - 지원서를 찾을 수 없음")
        @Test
        void fail_notFound() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> applicationFormService.getById(userId, formId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_APPLICATION_FORM.getMessage());
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));

            // when & then
            assertThatThrownBy(() -> applicationFormService.getById(2L, formId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(FORBIDDEN.getMessage());
        }
    }

    @DisplayName("지원서 저장")
    @Test
    void saveApplicationForm() {
        // given
        ApplicationFormRequest request = new ApplicationFormRequest("네이버", "경기도 성남시", null, null, null, APPLIED, null, null);
        given(userService.getUserById(userId)).willReturn(user);
        given(applicationFormRepository.save(any(ApplicationForm.class))).willReturn(applicationForm);

        // when
        applicationFormService.save(userId, request);

        // then
        then(applicationFormRepository).should().save(any(ApplicationForm.class));
    }

    @Nested
    @DisplayName("지원서 업데이트")
    class Update {
        ApplicationFormRequest request = new ApplicationFormRequest("카카오", "경기도 성남시", null, null, null, APPLIED, null, null);

        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));

            // when
            applicationFormService.update(userId, formId, request);

            // then
            ApplicationFormResponse form = applicationFormService.getById(userId, formId);
            assertThat(form.companyName()).isEqualTo("카카오");
        }

        @DisplayName("실패 - 지원서를 찾을 수 없음")
        @Test
        void fail_notFound() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> applicationFormService.update(userId, formId, request))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_APPLICATION_FORM.getMessage());
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));

            // when & then
            assertThatThrownBy(() -> applicationFormService.update(2L, formId, request))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(FORBIDDEN.getMessage());
        }
    }

    @Nested
    @DisplayName("지원서 삭제")
    class Delete {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));

            // when
            applicationFormService.delete(userId, formId);

            // then
            then(applicationFormRepository).should().delete(applicationForm);
        }

        @DisplayName("실패 - 지원서를 찾을 수 없음")
        @Test
        void fail_notFound() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> applicationFormService.delete(userId, formId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(NOT_FOUND_APPLICATION_FORM.getMessage());
            then(applicationFormRepository).should(never()).delete(any(ApplicationForm.class));
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));

            // when & then
            assertThatThrownBy(() -> applicationFormService.delete(2L, formId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(FORBIDDEN.getMessage());
            then(applicationFormRepository).should(never()).delete(any(ApplicationForm.class));
        }
    }
}
