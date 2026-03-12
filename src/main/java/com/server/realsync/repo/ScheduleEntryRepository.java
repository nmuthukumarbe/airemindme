package com.server.realsync.repo;


import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.server.realsync.entity.ScheduleEntry;
import com.server.realsync.entity.ScheduleEntryStatus;

/**
 * 
 */

public interface ScheduleEntryRepository extends JpaRepository<ScheduleEntry, Long> {

    List<ScheduleEntry> findByScheduleId(Long scheduleId);

    List<ScheduleEntry> findByOccurrenceDateBefore(LocalDateTime time);

    List<ScheduleEntry> findByStatus(ScheduleEntryStatus status);

}