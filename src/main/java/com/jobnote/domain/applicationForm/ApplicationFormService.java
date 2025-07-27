package com.jobnote.domain.applicationForm;

import com.jobnote.common.exception.JobNoteException;
import com.jobnote.domain.user.User;
import com.jobnote.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.jobnote.common.api.ResponseCode.NOT_FOUND_APPLICATION_FORM;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationFormService {

    private final ApplicationFormRepository applicationFormRepository;
    private final UserService userService;

    /* READ */
    public ApplicationFormResponse getById(Long userId, Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        return ApplicationFormMapper.fromApplicationForm(form);
    }

    public List<ApplicationFormResponse> getAll(Long userId) {
        return applicationFormRepository.findAllByUserId(userId).stream()
                .map(ApplicationFormMapper::fromApplicationForm)
                .collect(Collectors.toList());
    }

    /* CREATE */
    @Transactional
    public Long save(Long userId, ApplicationFormRequest request) {
        User user = userService.getUserById(userId);

        ApplicationForm saved = applicationFormRepository.save(ApplicationFormMapper.toApplicationForm(user, request));
        return saved.getId();
    }

    /* UPDATE */
    @Transactional
    public void update(Long userId, Long formId, ApplicationFormRequest request) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        form.update(request);
    }

    /* DELETE */
    @Transactional
    public void delete(Long userId, Long formId) {
        ApplicationForm form = getByIdOrThrow(formId);
        form.validateOwner(userId);

        applicationFormRepository.delete(form);
    }

    /* HELPER METHOD */
    private ApplicationForm getByIdOrThrow(Long formId) {
        return applicationFormRepository.findById(formId).orElseThrow(() ->
                new JobNoteException(NOT_FOUND_APPLICATION_FORM));
    }
}