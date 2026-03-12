/**
 * 
 */
package com.server.realsync.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.server.realsync.entity.ScheduleExecutionLog;

/**
 * 
 */

public interface ScheduleExecutionLogRepository extends JpaRepository<ScheduleExecutionLog, Long> {

	List<ScheduleExecutionLog> findByScheduleEntryId(Long scheduleEntryId);

}