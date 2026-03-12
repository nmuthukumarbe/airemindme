/**
 * 
 */
package com.server.realsync.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.server.realsync.entity.ScheduleExecutionLog;
import com.server.realsync.repo.ScheduleExecutionLogRepository;

/**
 * 
 */

@Service
public class ScheduleExecutionLogService {

    private final ScheduleExecutionLogRepository repository;

    public ScheduleExecutionLogService(ScheduleExecutionLogRepository repository) {
        this.repository = repository;
    }

    public ScheduleExecutionLog save(ScheduleExecutionLog log) {
        return repository.save(log);
    }

    public List<ScheduleExecutionLog> getByEntry(Long scheduleEntryId) {
        return repository.findByScheduleEntryId(scheduleEntryId);
    }
}