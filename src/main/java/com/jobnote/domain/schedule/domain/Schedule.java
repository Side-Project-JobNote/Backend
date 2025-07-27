package com.jobnote.domain.schedule.domain;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "schedule")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_form_id", nullable = false)
    private ApplicationForm applicationForm;

    @Column(nullable = false)
    private String title;

    @Lob
    private String memo;

    @Column(nullable = false)
    private LocalDateTime dateTime;

    @Builder
    public Schedule(
            ApplicationForm  applicationForm,
            String title,
            String memo,
            LocalDateTime dateTime
    ) {
        this.applicationForm = applicationForm;
        this.title = title;
        this.memo = memo;
        this.dateTime = dateTime;
    }

    public boolean isOwner(Long id) {
        return this.applicationForm.getUser().getId().equals(id);
    }
}
