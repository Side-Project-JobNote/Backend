package com.jobnote.domain.schedule.repository;

import com.jobnote.domain.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    /* 해당 유저의 일정 목록 조회 */
    @Query("select s from Schedule s join fetch s.applicationForm af join fetch af.user where af.user.id = :userId and s.dateTime between :startDate and :endDate")
    List<Schedule> findAllByUserIdAndDateTimeBetween(final Long userId, final LocalDateTime startDate, final LocalDateTime endDate);

    /* 여러 지원서의 일정 목록 전체 조회 */
    @Query("select s from Schedule s join fetch s.applicationForm af join fetch af.user where af.user.id = :userId and af.id in :formIds")
    List<Schedule> findAllByUserIdAndApplicationFormIdIn(final Long userId, final List<Long> formIds);

    /* 해당 지원서의 일정 목록 삭제 */
    void deleteAllByApplicationFormId(final Long formId);
}
