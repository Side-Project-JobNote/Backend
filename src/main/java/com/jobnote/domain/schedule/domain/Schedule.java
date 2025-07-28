package com.jobnote.domain.schedule.domain;

import com.jobnote.domain.applicationform.domain.ApplicationForm;
import com.jobnote.domain.common.BaseTimeEntity;
import com.jobnote.domain.schedule.dto.ScheduleRequest;
import com.jobnote.global.exception.JobNoteException;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import static com.jobnote.global.common.ResponseCode.FORBIDDEN;

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
            final ApplicationForm  applicationForm,
            final String title,
            final String memo,
            final LocalDateTime dateTime
    ) {
        this.applicationForm = applicationForm;
        this.title = title;
        this.memo = memo;
        this.dateTime = dateTime;
    }

    public void validateOwner(final Long userId) {
        if (!this.applicationForm.getUser().getId().equals(userId)) {
            throw new JobNoteException(FORBIDDEN);
        }
    }

    public void update(final ScheduleRequest request) {
        this.title = request.title();
        this.memo = request.memo();
        this.dateTime = request.dateTime();
    }
}
