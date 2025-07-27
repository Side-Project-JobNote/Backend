package com.jobnote.domain.applicationForm;

import com.jobnote.common.exception.JobNoteException;
import com.jobnote.domain.user.User;
import com.jobnote.domain.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.jobnote.common.api.ResponseCode.FORBIDDEN;
import static com.jobnote.common.api.ResponseCode.NOT_FOUND;
import static com.jobnote.domain.applicationForm.ApplicationFormStatus.APPLIED;
import static com.jobnote.domain.applicationForm.ApplicationFormStatus.DOCUMENT_PASSED;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

    @Nested
    @DisplayName("지원서 단건 조회")
    class GetById {
        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            given(applicationForm.isOwner(userId)).willReturn(true);

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
                    .hasMessage(NOT_FOUND.getMessage());
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            given(applicationForm.isOwner(userId)).willReturn(false);

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
        ApplicationFormRequest request = new ApplicationFormRequest("네이버", "02-1234-7812", "경기도 성남시", null, null, null, null, null, APPLIED);
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
        ApplicationFormRequest request = new ApplicationFormRequest("카카오", "02-1111-2222", "경기도 성남시", null, null, null, null, null, APPLIED);

        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            given(applicationForm.isOwner(userId)).willReturn(true);

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
                    .hasMessage(NOT_FOUND.getMessage());
            then(applicationForm).should(never()).update(request);
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            given(applicationForm.isOwner(userId)).willReturn(false);

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
            given(applicationForm.isOwner(userId)).willReturn(true);

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
                    .hasMessage(NOT_FOUND.getMessage());
            then(applicationFormRepository).should(never()).delete(any(ApplicationForm.class));
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
            given(applicationForm.isOwner(userId)).willReturn(false);

            // when & then
            assertThatThrownBy(() -> applicationFormService.delete(userId, formId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessage(FORBIDDEN.getMessage());
            then(applicationFormRepository).should(never()).delete(any(ApplicationForm.class));
        }
    }
}
