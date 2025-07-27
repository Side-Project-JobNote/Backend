package com.jobnote.domain.schedule.repository;

import com.jobnote.domain.schedule.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    @Query("select s from Schedule s join fetch s.applicationForm af join fetch af.user where af.user.id = :userId")
    List<Schedule> findAllByUserId(Long userId);
}
