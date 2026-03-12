package com.server.realsync.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.server.realsync.entity.Schedule;
import com.server.realsync.repo.ScheduleRepository;

/**
 * 
 */

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public Optional<Schedule> getById(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> getByAccount(Integer accountId) {
        return scheduleRepository.findByAccountId(accountId);
    }

    public List<Schedule> getDueSchedules() {
        return scheduleRepository.findByStartDatetimeBefore(LocalDateTime.now());
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }

}