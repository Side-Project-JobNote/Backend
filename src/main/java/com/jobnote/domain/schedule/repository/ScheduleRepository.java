package com.jobnote.domain.schedule.repository;

import com.jobnote.domain.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    /* 해당 유저의 일정 목록 조회 */
    @Query("select s from Schedule s join fetch s.applicationForm af join fetch af.user where af.user.id = :userId and s.dateTime between :startDate and :endDate")
    List<Schedule> findAllByUserIdAndDateTimeBetween(final Long userId, final LocalDateTime startDate, final LocalDateTime endDate);

    /* 해당 지원서의 일정 목록 조회 */
    @Query("select s from Schedule s join fetch s.applicationForm af join fetch af.user where af.user.id = :userId and af.id = :formId")
    List<Schedule> findAllByUserIdAndApplicationFormId(final Long userId, final Long formId);

    /* 여러 지원서의 일정 목록 전체 조회 */
    @Query("select s from Schedule s join fetch s.applicationForm af join fetch af.user where af.user.id = :userId and af.id in :formIds")
    List<Schedule> findAllByUserIdAndApplicationFormIds(@Param("userId") Long userId, @Param("formIds") List<Long> formIds);

    /* 해당 지원서의 일정 목록 삭제 */
    void deleteAllByApplicationFormId(Long formId);
}
