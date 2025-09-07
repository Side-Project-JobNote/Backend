package com.jobnote.domain.schedule.service;

import com.jobnote.ServiceUnitTest;
import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.repository.ApplicationFormRepository;
import com.jobnote.domain.schedule.domain.Schedule;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.repository.ScheduleRepository;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.domain.UserFixture;
import com.jobnote.global.exception.JobNoteException;
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
import java.util.List;
import java.util.Optional;

import static com.jobnote.domain.applicationform.domain.ApplicationFormStatus.APPLIED;
import static com.jobnote.domain.schedule.domain.ScheduleStatus.*;
import static com.jobnote.global.common.ResponseCode.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

class ScheduleServiceTest extends ServiceUnitTest {

    @InjectMocks private ScheduleService scheduleService;

    @Mock private ScheduleRepository scheduleRepository;
    @Mock private ApplicationFormRepository applicationFormRepository;

    private ApplicationForm applicationForm;
    private Schedule schedule;

    private final Long userId = 1L;
    private final Long formId = 1L;
    private final Long scheduleId = 1L;

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
        ReflectionTestUtils.setField(applicationForm, "id", formId);

        schedule = Schedule.builder()
                .applicationForm(applicationForm)
                .title("1차 면접")
                .memo("화상 면접")
                .dateTime(LocalDateTime.of(2025, 9, 10, 10, 0))
                .status(PLANNED)
                .build();
        ReflectionTestUtils.setField(schedule, "id", scheduleId);
    }

    @Nested
    @DisplayName("일정 단건 조회")
    class GetById {

        @DisplayName("성공")
        @Test
        void success() {
            // given
            given(scheduleRepository.findById(scheduleId)).willReturn(Optional.of(schedule));

            // when
            ScheduleResponse actual = scheduleService.getById(userId, formId, scheduleId);

            // then
            assertThat(actual.id()).isEqualTo(scheduleId);
            assertThat(actual.title()).isEqualTo("1차 면접");
        }

        @DisplayName("실패 - 일정을 찾을 수 없음")
        @Test
        void fail_notFound() {
            // given
            given(scheduleRepository.findById(scheduleId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> scheduleService.getById(userId, formId, scheduleId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessageContaining(NOT_FOUND_SCHEDULE.getMessage());
        }

        @DisplayName("실패 - 권한 없음")
        @Test
        void fail_forbidden() {
            // given
            given(scheduleRepository.findById(scheduleId)).willReturn(Optional.of(schedule));

            // when & then
            assertThatThrownBy(() -> scheduleService.getById(2L, formId, scheduleId))
                    .isInstanceOf(JobNoteException.class)
                    .hasMessageContaining(FORBIDDEN.getMessage());
        }
    }

    @DisplayName("일정 저장 성공")
    @Test
    void save_success() {
        ScheduleRequest request = new ScheduleRequest(null, "코딩 테스트", "온라인", LocalDateTime.now(), PLANNED);

        given(applicationFormRepository.findById(formId)).willReturn(Optional.of(applicationForm));
        given(scheduleRepository.save(any(Schedule.class))).willReturn(schedule);

        Long savedId = scheduleService.save(userId, formId, request);

        assertThat(savedId).isEqualTo(scheduleId);
        then(scheduleRepository).should().save(any(Schedule.class));
    }

    @DisplayName("일정 저장 실패 - 지원서 없음")
    @Test
    void save_notFoundForm() {
        ScheduleRequest request = new ScheduleRequest(null, "코딩 테스트", "온라인", LocalDateTime.now(), PLANNED);
        given(applicationFormRepository.findById(formId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleService.save(userId, formId, request))
                .isInstanceOf(JobNoteException.class)
                .hasMessageContaining(NOT_FOUND_APPLICATION_FORM.getMessage());
    }

    @DisplayName("일정 수정 성공")
    @Test
    void update_success() {
        ScheduleRequest updateReq = new ScheduleRequest(scheduleId, "최종 면접", "오프라인", LocalDateTime.now().plusDays(1), PROGRESS);

        given(scheduleRepository.findById(scheduleId)).willReturn(Optional.of(schedule));

        scheduleService.update(userId, formId, scheduleId, updateReq);

        assertThat(schedule.getTitle()).isEqualTo("최종 면접");
        assertThat(schedule.getStatus()).isEqualTo(PROGRESS);
    }

    @DisplayName("일정 삭제 성공")
    @Test
    void delete_success() {
        given(scheduleRepository.findById(scheduleId)).willReturn(Optional.of(schedule));
        willDoNothing().given(scheduleRepository).delete(schedule);

        scheduleService.delete(userId, formId, scheduleId);

        then(scheduleRepository).should().delete(schedule);
    }

    @DisplayName("해당 기간의 일정 조회 성공")
    @Test
    void getAll_success() {
        Page<Schedule> page = new PageImpl<>(List.of(schedule));

        given(scheduleRepository.findAllByUserIdAndDateTimeBetween(eq(userId), any(), any(), any(Pageable.class)))
                .willReturn(page);

        Page<ScheduleResponse> actual = scheduleService.getAll(
                userId, LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), Pageable.unpaged()
        );

        assertThat(actual.getContent()).hasSize(1);
        assertThat(actual.getContent().get(0).title()).isEqualTo("1차 면접");
    }

}
