package com.jobnote.domain.applicationform.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.repository.ApplicationFormRepository;
import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.domain.applicationform.dto.ApplicationFormResponse;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.service.ScheduleService;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.jobnote.global.common.ResponseCode.*;
import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.APPLIED;
import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.DOCUMENT_PASSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ApplicationFormServiceTest {

    @InjectMocks
    private ApplicationFormService applicationFormService;

    @Mock
    private ApplicationFormRepository applicationFormRepository;

    @Mock
    private UserService userService;

    @Mock
    private ScheduleService scheduleService;

    private User user;
    private ApplicationForm applicationForm;

    private final Long userId = 1L;
    private final Long formId = 1L;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
        applicationForm = mock(ApplicationForm.class);
    }

    @DisplayName("지원서 목록을 전체 조회한다.")
    @Test
    void getAllApplicationForms() {
        // given
        ApplicationForm form1 = ApplicationForm.builder().user(user).companyName("네이버").status(APPLIED).build();
        ApplicationForm form2 = ApplicationForm.builder().user(user).companyName("카카오").status(DOCUMENT_PASSED).build();
        List<ApplicationForm> expectedResult = Arrays.asList(form1, form2);

        given(applicationFormRepository.findAllByUserId(userId)).willReturn(expectedResult);

        // when
        List<ApplicationFormResponse> actualResult = applicationFormService.getAll(userId);

        // then
        assertThat(actualResult).hasSize(2);
        assertThat(actualResult.get(0).companyName()).isEqualTo("네이버");
        then(applicationFormRepository).should().findAllByUserId(userId);
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

        given(applicationFormRepository.findAllByUserId(userId)).willReturn(forms);

        ScheduleResponse schedule1 = new ScheduleResponse(101L, "지원서 제출", "오전", LocalDateTime.of(2025, 8, 1, 10, 0));
        ScheduleResponse schedule2 = new ScheduleResponse(102L, "코딩테스트", "연습문제 풀이", LocalDateTime.of(2025, 8, 2, 9, 0));
        ScheduleResponse schedule3 = new ScheduleResponse(103L, "2차 면접", "예상질문지 복습", LocalDateTime.of(2025, 8, 5, 9, 0));

        given(scheduleService.getAllGroupedByApplicationFormIds(eq(userId), eq(List.of(1L, 2L))))
                .willReturn(Map.of(
                        1L, List.of(schedule1),
                        2L, List.of(schedule2, schedule3)
                ));

        // when
        List<ApplicationFormResponse> actualResult = applicationFormService.getAll(userId);

        // then
        assertThat(actualResult).hasSize(2);

        ApplicationFormResponse result1 = actualResult.get(0);
        assertThat(result1.companyName()).isEqualTo("네이버");
        assertThat(result1.schedules()).hasSize(1);
        assertThat(result1.schedules().get(0).title()).isEqualTo("지원서 제출");

        ApplicationFormResponse result2 = actualResult.get(1);
        assertThat(result2.companyName()).isEqualTo("카카오");
        assertThat(result2.schedules()).hasSize(2);
        assertThat(result2.schedules().get(0).title()).isEqualTo("코딩테스트");

        then(applicationFormRepository).should().findAllByUserId(userId);
        then(scheduleService).should().getAllGroupedByApplicationFormIds(eq(userId), eq(List.of(1L, 2L)));
    }

    @Nested
    @DisplayName("지원서 단건 조회")
    class GetById {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            willDoNothing().given(applicationForm).validateOwner(userId);

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
            doThrow(new JobNoteException(FORBIDDEN))
                    .when(applicationForm)
                    .validateOwner(userId);

            // when & then
            assertThatThrownBy(() -> applicationFormService.getById(userId, formId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(FORBIDDEN.getMessage());
        }
    }

    @DisplayName("지원서 저장")
    @Test
    void saveApplicationForm() {
        // given
        ApplicationFormRequest request = new ApplicationFormRequest("네이버", "02-1234-7812", "경기도 성남시", null, null, null, null, null, APPLIED, null);
        given(userService.getUserById(userId)).willReturn(user);
        given(applicationFormRepository.save(any(ApplicationForm.class))).willReturn(applicationForm);

        // when
        applicationFormService.save(userId, request);

        // then
        then(applicationFormRepository).should().save(any(ApplicationForm.class));
    }

    @Nested
    @DisplayName("지원서 수정")
    class Update {
        ApplicationFormRequest request = new ApplicationFormRequest("카카오", "02-1111-2222", "경기도 성남시", null, null, null, null, null, APPLIED, null);

        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            willDoNothing().given(applicationForm).validateOwner(userId);

            // when
            applicationFormService.update(userId, formId, request);

            // then
            then(applicationFormRepository).should().findById(formId);
            then(applicationForm).should().update(request);
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
            then(applicationForm).should(never()).update(request);
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            doThrow(new JobNoteException(FORBIDDEN))
                    .when(applicationForm)
                    .validateOwner(userId);

            // when & then
            assertThatThrownBy(() -> applicationFormService.update(userId, formId, request))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(FORBIDDEN.getMessage());
            then(applicationForm).should(never()).update(request);
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
            willDoNothing().given(applicationForm).validateOwner(userId);

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
            doThrow(new JobNoteException(FORBIDDEN))
                    .when(applicationForm)
                    .validateOwner(userId);

            // when & then
            assertThatThrownBy(() -> applicationFormService.delete(userId, formId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(FORBIDDEN.getMessage());
            then(applicationFormRepository).should(never()).delete(any(ApplicationForm.class));
        }
    }
}
