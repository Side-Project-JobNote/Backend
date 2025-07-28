package com.jobnote.domain.applicationform.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationform.repository.ApplicationFormRepository;
import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.domain.applicationform.dto.ApplicationFormResponse;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_APPLICATION_FORM;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationFormService {

    private final ApplicationFormRepository applicationFormRepository;
    private final UserService userService;

    /* READ */
    public ApplicationFormResponse getById(final Long userId, final Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        return ApplicationFormResponse.from(form);
    }

    public List<ApplicationFormResponse> getAll(final Long userId) {
        return applicationFormRepository.findAllByUserId(userId).stream()
                .map(ApplicationFormResponse::from)
                .collect(Collectors.toList());
    }

    /* CREATE */
    @Transactional
    public Long save(final Long userId, final ApplicationFormRequest request) {
        User user = userService.getUserById(userId);

        ApplicationForm saved = applicationFormRepository.save(request.toEntity(user));
        return saved.getId();
    }

    /* UPDATE */
    @Transactional
    public void update(final Long userId, final Long formId, final ApplicationFormRequest request) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        form.update(request);
    }

    /* DELETE */
    @Transactional
    public void delete(final Long userId, final Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        applicationFormRepository.delete(form);
    }

    /* HELPER METHOD */
    public ApplicationForm getByIdOrThrow(final Long formId) {
        return applicationFormRepository.findById(formId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_APPLICATION_FORM));
    }
}