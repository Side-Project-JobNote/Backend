package com.jobnote.domain.schedule.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.schedule.domain.Schedule;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.repository.ScheduleRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_SCHEDULE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    /* READ */
    public ScheduleResponse getById(final Long userId, final Long formId, final Long scheduleId) {
        Schedule schedule = getByIdOrThrow(scheduleId);
        schedule.validateOwner(userId);
        schedule.validateBelongsTo(formId);

        return ScheduleResponse.from(schedule);
    }

    public List<ScheduleResponse> getAll(final Long userId, final LocalDateTime startDate, final LocalDateTime endDate) {
        return scheduleRepository.findAllByUserIdAndDateTimeBetween(userId, startDate, endDate).stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    public List<ScheduleResponse> getAllByApplicationFormId(final Long userId, final Long formId) {
        return scheduleRepository.findAllByUserIdAndApplicationFormId(userId, formId).stream()
                .map(ScheduleResponse::from)
                .toList();
    }

    public Map<Long, List<ScheduleResponse>> getAllGroupedByApplicationFormIds(final Long userId, final List<Long> formIds) {
        List<Schedule> schedules = scheduleRepository.findAllByUserIdAndApplicationFormIds(userId, formIds);

        return schedules.stream()
                .collect(Collectors.groupingBy(
                        // 각 일정이 속한 지원서의 ID를 기준으로 그룹핑
                        schedule -> schedule.getApplicationForm().getId(),
                        // 그룹핑된 일정들을 ScheduleResponse로 변환하여 List로 수집
                        Collectors.mapping(ScheduleResponse::from, Collectors.toList())
                ));
    }

    /* CREATE */
    @Transactional
    public Long save(final Long userId, final ApplicationForm form, final ScheduleRequest request) {
        form.validateOwner(userId);

        Schedule saved = scheduleRepository.save(request.toEntity(form));
        return saved.getId();
    }

    @Transactional
    public void saveAll(final Long userId, final ApplicationForm form, final List<ScheduleRequest> requests) {
        form.validateOwner(userId);

        List<Schedule> schedules = requests.stream()
                .map(req -> req.toEntity(form))
                .toList();

        scheduleRepository.saveAll(schedules);
    }

    /* UPDATE */
    @Transactional
    public void update(final Long userId, final Long formId, final Long scheduleId, final ScheduleRequest request) {
        Schedule schedule = getByIdOrThrow(scheduleId);
        schedule.validateOwner(userId);
        schedule.validateBelongsTo(formId);

        schedule.update(request);
    }

    @Transactional
    public void updateAll(final Long userId, final ApplicationForm form, final List<ScheduleRequest> requests) {
        // 기존 일정 조회
        List<Schedule> existsSchedules = scheduleRepository.findAllByUserIdAndApplicationFormId(userId, form.getId());

        // 요청 일정 ID 목록
        Set<Long> requestsIds = requests.stream()
                .map(ScheduleRequest::id)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        // 삭제 : 기존 일정 중 요청에 없는 일정 삭제
        for (Schedule schedule : existsSchedules) {
            if (!requestsIds.contains(schedule.getId())) {
                schedule.validateOwner(userId);
                schedule.validateBelongsTo(form.getId());
                scheduleRepository.delete(schedule);
            }
        }

        // 업데이트 또는 신규 생성
        for (ScheduleRequest req : requests) {
            if (req.id() != null) {
                // 업데이트
                Schedule schedule = getByIdOrThrow(req.id());
                schedule.validateOwner(userId);
                schedule.validateBelongsTo(form.getId());
                schedule.update(req);
            } else {
                // 신규 생성
                Schedule newSchedule = req.toEntity(form);
                scheduleRepository.save(newSchedule);
            }
        }
    }

    /* DELETE */
    @Transactional
    public void delete(final Long userId, final Long formId, final Long scheduleId) {
        Schedule schedule = getByIdOrThrow(scheduleId);
        schedule.validateOwner(userId);
        schedule.validateBelongsTo(formId);

        scheduleRepository.delete(schedule);
    }

    @Transactional
    public void deleteAllByApplicationFormId(final Long formId) {
        scheduleRepository.deleteAllByApplicationFormId(formId);
    }

    /* HELPER METHOD */
    private Schedule getByIdOrThrow(final Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_SCHEDULE));
    }
}
