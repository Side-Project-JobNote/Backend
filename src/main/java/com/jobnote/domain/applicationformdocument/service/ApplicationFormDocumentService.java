package com.jobnote.domain.applicationformdocument.service;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.applicationformdocument.domain.ApplicationFormDocument;
import com.jobnote.domain.applicationformdocument.dto.ApplicationFormDocumentRequest;
import com.jobnote.domain.applicationformdocument.repository.ApplicationFormDocumentRepository;
import com.jobnote.domain.document.domain.Document;
import com.jobnote.domain.document.dto.DocumentSimpleResponse;
import com.jobnote.domain.document.repository.DocumentRepository;
import com.jobnote.global.exception.JobNoteException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.jobnote.global.common.ResponseCode.NOT_FOUND_DOCUMENT;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApplicationFormDocumentService {

    private final ApplicationFormDocumentRepository applicationFormDocumentRepository;
    private final DocumentRepository documentRepository;

    /* READ */
    public List<ApplicationFormDocument> getAllByApplicationFormId(final Long userId, final Long formId) {
        return applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, List.of(formId));
    }

    public List<DocumentSimpleResponse> getSimpleResponsesByApplicationFormId(final Long userId, final Long formId) {
        return applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, List.of(formId)).stream()
                .map(afd -> DocumentSimpleResponse.from(afd.getDocument()))
                .toList();
    }

    public Map<Long, List<DocumentSimpleResponse>> getSimpleResponsesGroupedByApplicationFormIds(final Long userId, final List<Long> formIds) {
        List<ApplicationFormDocument> list = applicationFormDocumentRepository.findAllByUserIdAndApplicationFormIdIn(userId, formIds);

        return list.stream()
                .collect(Collectors.groupingBy(
                        // 각 문서가 속한 지원서의 ID를 기준으로 그룹핑
                        document -> document.getApplicationForm().getId(),
                        // 그룹핑된 문서들을 DocumentSimpleResponse로 변환하여 List로 수집
                        Collectors.mapping(
                                afd -> DocumentSimpleResponse.from(afd.getDocument()),
                                Collectors.toList()
                        )
                ));
    }

    /* CREATE */
    @Transactional
    public void saveAll(final ApplicationForm form, final List<ApplicationFormDocumentRequest> requests) {
        for (ApplicationFormDocumentRequest request : requests) {
            Document document = getByIdOrThrow(request.documentId());
            ApplicationFormDocument entity = request.toEntity(form, document);
            applicationFormDocumentRepository.save(entity);
        }
    }

    /* DELETE */
    @Transactional
    public void deleteAllByApplicationFormId(final Long formId) {
        applicationFormDocumentRepository.deleteAllByApplicationFormId(formId);
    }

    @Transactional
    public void delete(final ApplicationFormDocument document) {
        applicationFormDocumentRepository.delete(document);
    }

    /* HELPER METHOD */
    private Document getByIdOrThrow(final Long docId) {
        return documentRepository.findById(docId)
                .orElseThrow(() -> new JobNoteException(NOT_FOUND_DOCUMENT));
    }
}
