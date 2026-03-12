package com.server.realsync.services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.server.realsync.entity.EntityType;
import com.server.realsync.entity.MessageQueue;
import com.server.realsync.entity.QueueChannel;
import com.server.realsync.entity.ScheduleEntry;

/**
 * 
 */

@Component
public class ReminderScheduler {

    private final ScheduleEntryService scheduleEntryService;
    private final MessageQueueService queueService;

    public ReminderScheduler(
            ScheduleEntryService scheduleEntryService,
            MessageQueueService queueService) {

        this.scheduleEntryService = scheduleEntryService;
        this.queueService = queueService;
    }

    //@Scheduled(fixedDelay = 60000)
    public void loadDueSchedules() {

        List<ScheduleEntry> entries =
                scheduleEntryService.getDueEntries();

        for (ScheduleEntry entry : entries) {

            MessageQueue job = new MessageQueue();

            job.setEntityType(EntityType.SCHEDULE);
            job.setEntityEntryId(entry.getId());
            job.setChannel(QueueChannel.WHATSAPP);
            job.setPriority(1); // reminder = high priority

            queueService.save(job);
        }
    }
}