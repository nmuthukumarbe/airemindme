package com.server.realsync.services;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.server.realsync.entity.*;
import com.server.realsync.repo.*;

@Service
public class GreetingService {

    @Autowired
    private GreetingRepository repo;
    @Autowired
    private CustomerRepository customerRepo;
    @Autowired
    private ScheduleEntryRepository scheduleEntryRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    public List<Greeting> getByAccountId(Integer accountId) {
        return repo.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    public Optional<Greeting> getById(Integer id, Integer accountId) {
        return repo.findByIdAndAccountId(id, accountId);
    }

    @Transactional
    public Greeting save(Greeting greeting) {
        if (greeting.getStatus() == null) {
            greeting.setStatus("Scheduled");
        }

        Greeting saved = repo.save(greeting);
        Long greetingId = saved.getId().longValue();

        scheduleEntryRepository.deleteBySourceIdAndSourceTypeAndStatusNot(
                greetingId, "GREETING", ScheduleEntryStatus.COMPLETED);

        scheduleRepository.deleteBySourceTypeAndSourceId("GREETING", greetingId);

        createGreetingSchedules(saved);

        return saved;
    }

    private void createGreetingSchedules(Greeting g) {
        LocalTime time = g.getGreetingTime() != null ? g.getGreetingTime() : LocalTime.of(9, 0);
        LocalDateTime dateTime = LocalDateTime.of(g.getGreetingDate(), time);

        // Group customers
        if (g.getCustomerGroupId() != null) {
            List<Customer> customers = customerRepo.findByAccountIdAndCustomerGroupId(
                    g.getAccountId(), g.getCustomerGroupId(), Pageable.unpaged()).getContent();

            Schedule schedule = createGreetingSchedule(g, dateTime);
            for (Customer c : customers) {
                createEntry(schedule, g, c.getId(), dateTime);
            }
        }
        // Single customer
        else if (g.getCustomerId() != null) {
            Schedule schedule = createGreetingSchedule(g, dateTime);
            createEntry(schedule, g, g.getCustomerId(), dateTime);
        }
    }

    private Schedule createGreetingSchedule(Greeting g, LocalDateTime dateTime) {
        Schedule schedule = new Schedule();
        schedule.setAccountId(g.getAccountId());
        schedule.setTitle(g.getGreetingType() + " Greeting");
        schedule.setRemarks(g.getMessage());
        schedule.setSourceType("GREETING");
        schedule.setSourceId(g.getId().longValue());
        schedule.setType(ScheduleType.ONE_TIME);
        schedule.setStartDatetime(dateTime);

        return scheduleRepository.save(schedule);
    }

    private void createEntry(Schedule schedule, Greeting g, Integer customerId, LocalDateTime time) {
        ScheduleEntry e = new ScheduleEntry();
        e.setScheduleId(schedule.getId());
        e.setSourceType("GREETING");
        e.setSourceId(g.getId().longValue());
        e.setCustomerId(customerId.longValue());
        e.setOccurrenceDate(time);
        e.setStatus(ScheduleEntryStatus.PENDING);
        e.setRemarks(g.getMessage());

        scheduleEntryRepository.save(e);
    }

    @Transactional
    public void delete(Integer id, Integer accountId) {
        Long greetingId = id.longValue();
        scheduleEntryRepository.deleteBySourceIdAndSourceType(greetingId, "GREETING");
        scheduleRepository.deleteBySourceTypeAndSourceId("GREETING", greetingId);
        repo.deleteByIdAndAccountId(id, accountId);
    }

    public List<ScheduleEntry> getGreetingEntries(Integer greetingId) {
        return scheduleEntryRepository.findBySourceTypeAndSourceId("GREETING", greetingId.longValue());
    }

    public long countByAccountId(Integer accountId) {
        return repo.countByAccountId(accountId);
    }

    public List<Greeting> getGreetingsForCustomer(Customer customer) {
        List<Greeting> greetings = new ArrayList<>(repo.findByCustomerIdAndAccountId(customer.getId(), customer.getAccountId()));
        String groupIdStr = customer.getCustomerGroupId();
        if (groupIdStr != null && !groupIdStr.trim().isEmpty()) {
            try {
                List<Integer> groupIds = Arrays.stream(groupIdStr.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                if (!groupIds.isEmpty()) {
                    List<Greeting> groupGreetings = repo.findByAccountIdAndCustomerGroupIdIn(customer.getAccountId(), groupIds);
                    greetings.addAll(groupGreetings);
                }
            } catch (Exception e) {
                // Ignore parsing errors
            }
        }
        List<Greeting> uniqueGreetings = greetings.stream().distinct().collect(Collectors.toList());
        uniqueGreetings.sort((g1, g2) -> g2.getGreetingDate().compareTo(g1.getGreetingDate()));
        return uniqueGreetings;
    }
}