package com.server.realsync.services;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.server.realsync.entity.*;
import com.server.realsync.repo.*;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository repo;
    @Autowired
    private ScheduleEntryRepository scheduleEntryRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Transactional
    public Reminder save(Reminder reminder) {
        if (reminder.getStatus() == null) {
            reminder.setStatus("Scheduled");
        }
        Reminder saved = repo.save(reminder);
        Long reminderId = saved.getId().longValue();

        scheduleEntryRepository.deleteByReminderIdAndStatusNot(reminderId, ScheduleEntryStatus.COMPLETED);
        scheduleRepository.deleteBySourceTypeAndSourceId("REMINDER", reminderId);

        createSchedules(saved);
        return saved;
    }

    private void createSchedules(Reminder r) {
        LocalTime time = r.getReminderTime() != null ? r.getReminderTime() : LocalTime.of(9, 0);
        LocalDateTime base = LocalDateTime.of(r.getReminderDate(), time);

        // One-time logic
        if (!"recurring".equalsIgnoreCase(r.getReminderType())) {
            Schedule schedule = createParentSchedule(r, base);
            createEntryForSchedule(schedule, r, base);
            return;
        }

        // Recurring logic
        int count = r.getTotalOccurrences() != null ? r.getTotalOccurrences() : 1;
        String freq = r.getFrequency() != null ? r.getFrequency() : "none";

        Schedule schedule = createParentSchedule(r, base);

        // Add recurrence metadata to parent schedule
        schedule.setRepeatCount(count);

        switch (freq.toLowerCase()) {

            case "daily":
                schedule.setRepeatEvery(RepeatEvery.DAILY);
                break;

            case "weekly":
                schedule.setRepeatEvery(RepeatEvery.WEEKLY);
                break;

            case "monthly":
                schedule.setRepeatEvery(RepeatEvery.MONTHLY);
                break;

            case "yearly":
                schedule.setRepeatEvery(RepeatEvery.YEARLY);
                break;

            default:
                schedule.setRepeatEvery(RepeatEvery.NONE);
                break;
        }

        schedule.setType(ScheduleType.RECURRING);
        scheduleRepository.save(schedule);

        for (int i = 0; i < count; i++) {
            LocalDateTime next = switch (freq.toLowerCase()) {
                case "daily" -> base.plusDays(i);
                case "weekly" -> base.plusWeeks(i);
                case "monthly" -> base.plusMonths(i);
                case "yearly" -> base.plusYears(i);
                default -> base;
            };
            createEntryForSchedule(schedule, r, next);
        }
    }

    private Schedule createParentSchedule(Reminder r, LocalDateTime startTime) {
        Schedule schedule = new Schedule();
        schedule.setAccountId(r.getAccountId());
        schedule.setCustomerId(r.getCustomerId());
        schedule.setTitle(r.getTitle());
        schedule.setRemarks(r.getMessage());
        schedule.setSourceType("REMINDER");
        schedule.setSourceId(r.getId().longValue());
        schedule.setStartDatetime(startTime);
        schedule.setType(ScheduleType.ONE_TIME); // Default to one-time, updated in loop if recurring

        return scheduleRepository.save(schedule);
    }

    private void createEntryForSchedule(Schedule schedule, Reminder r, LocalDateTime time) {
        ScheduleEntry e = new ScheduleEntry();
        e.setScheduleId(schedule.getId());
        e.setReminderId(r.getId().longValue());
        if (r.getCustomerId() != null) {
            e.setCustomerId(r.getCustomerId().longValue());
        }
        e.setOccurrenceDate(time);
        e.setStatus(ScheduleEntryStatus.PENDING);
        e.setAmount(r.getAmount() != null ? BigDecimal.valueOf(r.getAmount()) : null);
        e.setRemarks(r.getMessage());
        e.setSourceType("REMINDER");
        e.setSourceId(r.getId().longValue());
        scheduleEntryRepository.save(e);
    }

    public List<Reminder> getByAccountId(Integer accountId) {
        return repo.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public List<Reminder> getByCustomerId(Integer customerId, Integer accountId) {
        return repo.findByCustomerIdAndAccountId(customerId, accountId);
    }

    public Optional<Reminder> getById(Integer id, Integer accountId) {
        return repo.findByIdAndAccountId(id, accountId);
    }

    /** Hard delete */

    @Transactional
    public void cancelRemainingInstallments(Integer reminderId, Integer accountId) {

        Reminder reminder = getById(reminderId, accountId)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        scheduleEntryRepository.deleteByReminderIdAndStatusNot(reminder.getId().longValue(),
                ScheduleEntryStatus.COMPLETED);
    }

    @Transactional

    public void delete(Integer id, Integer accountId) {

        Optional<Reminder> opt = getById(id, accountId);

        if (opt.isEmpty()) {

            throw new RuntimeException("Reminder not found");

        }

        Long reminderId = opt.get().getId().longValue();

        // 🔥 STEP 1: delete execution rows

        scheduleEntryRepository.deleteByReminderIdAndStatusNot(

                reminderId,

                ScheduleEntryStatus.COMPLETED);

        scheduleRepository.deleteBySourceTypeAndSourceId("REMINDER", reminderId);

        // 🔥 STEP 2: delete reminder

        repo.deleteByIdAndAccountId(id, accountId);

    }

    public boolean hasPaidEntries(Long reminderId) {

        return scheduleEntryRepository.existsByReminderIdAndStatus(
                reminderId,
                ScheduleEntryStatus.COMPLETED);
    }

    /** Count reminders currently scheduled */

    public long countScheduledByAccountId(Integer accountId) {

        return repo.countByAccountIdAndStatus(accountId, "Scheduled");

    }

    /** Count total reminders sent today (Native Query) */

    public long countSentToday(Integer accountId) {

        return repo.countSentToday(accountId);

    }

    public void reschedule(Integer id, Integer accountId, String date, String time) {

        Reminder reminder = getById(id, accountId)

                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setReminderDate(LocalDate.parse(date));

        reminder.setReminderTime(LocalTime.parse(time));

        save(reminder);

    }

    public void makeRecurring(Integer id, Integer accountId) {

        Reminder reminder = getById(id, accountId)

                .orElseThrow(() -> new RuntimeException("Reminder not found"));

        reminder.setRecurring(true);

        save(reminder);

    }

    public List<Reminder> getTop3UpcomingByAccountId(Integer accountId) {
        return repo.findTop3UpcomingByAccountId(accountId);
    }

}
