package com.jobnote.domain.schedule.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.service.ApplicationFormService;
import com.jobnote.domain.schedule.domain.Schedule;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.repository.ScheduleRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_SCHEDULE;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ApplicationFormService applicationFormService;

    /* READ */
    public ScheduleResponse getById(final Long userId, final Long scheduleId) {
        Schedule schedule = getByIdOrThrow(scheduleId);
        schedule.validateOwner(userId);

        return ScheduleResponse.from(schedule);
    }

    public List<ScheduleResponse> getAll(final Long userId) {
        return scheduleRepository.findAllByUserId(userId).stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    /* CREATE */
    @Transactional
    public Long save(final Long userId, final Long formId, final ScheduleRequest request) {
        ApplicationForm form = applicationFormService.getByIdOrThrow(formId);
        form.validateOwner(userId);

        Schedule saved = request.toEntity(form);
        return saved.getId();
    }

    /* UPDATE */
    @Transactional
    public void update(final Long userId, final Long scheduleId, final ScheduleRequest request) {
        Schedule schedule = getByIdOrThrow(scheduleId);
        schedule.validateOwner(userId);

        schedule.update(request);
    }

    /* DELETE */
    @Transactional
    public void delete(final Long userId, final Long scheduleId) {
        Schedule schedule = getByIdOrThrow(scheduleId);
        schedule.validateOwner(userId);

        scheduleRepository.delete(schedule);
    }

    /* HELPER METHOD */
    public Schedule getByIdOrThrow(final Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_SCHEDULE));
    }
}
