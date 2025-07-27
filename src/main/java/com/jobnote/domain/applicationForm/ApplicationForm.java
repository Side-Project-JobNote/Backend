package com.jobnote.domain.applicationForm;

import com.jobnote.domain.user.User;
import com.jobnote.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private String companyTel;

    private String companyAddress;

    private String companyUrl;

    private String companyEmail;

    private String companyScale;

    private String position;

    @Lob
    private String memo;

    @Enumerated(EnumType.STRING)
    private ApplicationFormStatus status;

    @Builder
    public ApplicationForm(
            User user,
            String companyName,
            String companyTel,
            String companyAddress,
            String companyUrl,
            String companyEmail,
            String companyScale,
            String position,
            String memo,
            ApplicationFormStatus status
    ) {
      this.user = user;
      this.companyName = companyName;
      this.companyTel = companyTel;
      this.companyAddress = companyAddress;
      this.companyUrl = companyUrl;
      this.companyEmail = companyEmail;
      this.companyScale = companyScale;
      this.position = position;
      this.memo = memo;
      this.status = status;
    }

    public boolean isOwner(Long id) {
        return this.user.getId().equals(id);
    }

    public void update(ApplicationFormRequest request) {
        this.companyName = request.companyName();
        this.companyTel = request.companyTel();
        this.companyAddress = request.companyAddress();
        this.companyUrl = request.companyUrl();
        this.companyEmail = request.companyEmail();
        this.companyScale = request.companyScale();
        this.position = request.position();
        this.memo = request.memo();
        this.status = request.status();
    }
}
