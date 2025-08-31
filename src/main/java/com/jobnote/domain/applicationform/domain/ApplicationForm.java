package com.jobnote.domain.applicationform.domain;

import com.jobnote.domain.applicationform.dto.ApplicationFormRequest;
import com.jobnote.global.exception.JobNoteException;
import com.jobnote.domain.user.domain.User;
import com.jobnote.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import static com.jobnote.global.common.ResponseCode.FORBIDDEN;

@Entity
@Getter
@Table(name = "application_form")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApplicationForm extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_form_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String companyName;

    private String companyAddress;

    private String companyUrl;

    private String companyScale;

    private String position;

    @Enumerated(EnumType.STRING)
    private ApplicationFormStatus status;

    @Builder
    public ApplicationForm(
            final User user,
            final String companyName,
            final String companyAddress,
            final String companyUrl,
            final String companyScale,
            final String position,
            final ApplicationFormStatus status
    ) {
      this.user = user;
      this.companyName = companyName;
      this.companyAddress = companyAddress;
      this.companyUrl = companyUrl;
      this.companyScale = companyScale;
      this.position = position;
      this.status = status;
    }

    public void validateOwner(final Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new JobNoteException(FORBIDDEN);
        }
    }

    public void update(final ApplicationFormRequest request) {
        this.companyName = request.companyName();
        this.companyAddress = request.companyAddress();
        this.companyUrl = request.companyUrl();
        this.companyScale = request.companyScale();
        this.position = request.position();
        this.status = request.status();
    }
}
