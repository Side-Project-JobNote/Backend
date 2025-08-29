package com.jobnote.domain.applicationform.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.repository.ApplicationFormRepository;
import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.domain.applicationform.dto.ApplicationFormResponse;
import com.jobnote.domain.applicationformdocument.service.ApplicationFormDocumentService;
import com.jobnote.domain.document.dto.DocumentSimpleResponse;
import com.jobnote.domain.schedule.dto.ScheduleResponse;
import com.jobnote.domain.schedule.service.ScheduleService;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final ApplicationFormDocumentService applicationFormDocumentService;

    /* READ */
    public ApplicationFormResponse getById(final Long userId, final Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        List<ScheduleResponse> schedules = scheduleService.getAllByApplicationFormId(userId, formId);
        List<DocumentSimpleResponse> documents = applicationFormDocumentService.getSimpleResponsesByApplicationFormId(userId, formId);

        return ApplicationFormResponse.from(form, schedules, documents);
    }

    public Page<ApplicationFormResponse> getAll(final Long userId, final Pageable pageable) {
        Page<ApplicationForm> forms = applicationFormRepository.findAllByUserId(userId, pageable);
        List<Long> formIds = forms.map(ApplicationForm::getId).toList();

        Map<Long, List<ScheduleResponse>> schedulesByFormId = scheduleService.getAllGroupedByApplicationFormIds(userId, formIds);
        Map<Long, List<DocumentSimpleResponse>> documentsByFormId = applicationFormDocumentService.getSimpleResponsesGroupedByApplicationFormIds(userId, formIds);

        return forms.map(form -> ApplicationFormResponse.from(
                form,
                schedulesByFormId.getOrDefault(form.getId(), List.of()),
                documentsByFormId.getOrDefault(form.getId(), List.of()))
        );
    }

    /* CREATE */
    @Transactional
    public Long save(final Long userId, final ApplicationFormRequest request) {
        User user = userService.getUserById(userId);

        // 지원서 생성
        ApplicationForm form = applicationFormRepository.save(request.toEntity(user));

        // 일정 등록
        scheduleService.saveAll(userId, form, request.schedules());

        // 연결된 문서 등록
        applicationFormDocumentService.saveAll(form, request.documents());

        return form.getId();
    }

    /* UPDATE */
    @Transactional
    public void update(final Long userId, final Long formId, final ApplicationFormRequest request) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        // 지원서 업데이트
        form.update(request);

        // 일정 업데이트
        scheduleService.updateAll(userId, form, request.schedules());

        // 연결된 문서 업데이트
        applicationFormDocumentService.updateAll(userId, form, request.documents());
    }

    /* DELETE */
    @Transactional
    public void delete(final Long userId, final Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        scheduleService.deleteAllByApplicationFormId(form.getId());
        applicationFormDocumentService.deleteAllByApplicationFormId(form.getId());
        applicationFormRepository.delete(form);
    }

    /* HELPER METHOD */
    public ApplicationForm getByIdOrThrow(final Long formId) {
        return applicationFormRepository.findById(formId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_APPLICATION_FORM));
    }
}
