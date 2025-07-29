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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_APPLICATION_FORM;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationFormService {

    private final ApplicationFormRepository applicationFormRepository;
    private final UserService userService;
    private final ScheduleService scheduleService;

    /* READ */
    public ApplicationFormResponse getById(final Long userId, final Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        List<ScheduleResponse> schedules = scheduleService.getAllByApplicationFormId(userId, formId);

        return ApplicationFormResponse.from(form, schedules);
    }

    public List<ApplicationFormResponse> getAll(final Long userId) {
        List<ApplicationForm> forms = applicationFormRepository.findAllByUserId(userId);
        List<Long> formIds = forms.stream().map(ApplicationForm::getId).toList();

        Map<Long, List<ScheduleResponse>> schedulesByFormId = scheduleService.getAllGroupedByApplicationFormIds(userId, formIds);

        return forms.stream()
                .map(form -> ApplicationFormResponse.from(form, schedulesByFormId.getOrDefault(form.getId(), List.of())))
                .toList();
    }

    /* CREATE */
    @Transactional
    public Long save(final Long userId, final ApplicationFormRequest request) {
        User user = userService.getUserById(userId);

        ApplicationForm form = applicationFormRepository.save(request.toEntity(user));
        scheduleService.saveAll(userId, form, request.schedules());

        return form.getId();
    }

    /* UPDATE */
    @Transactional
    public void update(final Long userId, final Long formId, final ApplicationFormRequest request) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        form.update(request);
        scheduleService.updateAll(userId, form, request.schedules());
    }

    /* DELETE */
    @Transactional
    public void delete(final Long userId, final Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        scheduleService.deleteAllByApplicationFormId(form.getId());
        applicationFormRepository.delete(form);
    }

    /* HELPER METHOD */
    public ApplicationForm getByIdOrThrow(final Long formId) {
        return applicationFormRepository.findById(formId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_APPLICATION_FORM));
    }
}
