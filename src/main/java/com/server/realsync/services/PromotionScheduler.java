package com.server.realsync.services;

import com.server.realsync.entity.*;
import com.server.realsync.repo.PromotionExecutionLogRepository;
import com.server.realsync.repo.PromotionItemRepository;
import com.server.realsync.repo.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class PromotionScheduler {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionEntryService entryService;

    @Autowired
    private PromotionExecutionLogRepository logRepository;

    @Autowired
    private CustomerService customerService;

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void executeScheduledPromotions() {
        LocalDateTime now = LocalDateTime.now();
        List<Promotion> scheduled = promotionRepository.findByStatusAndScheduledAtLessThanEqual("SCHEDULED", now);

        for (Promotion promo : scheduled) {
            try {
                // Prevent duplicate execution/race condition by changing status first
                promo.setStatus("ACTIVE");
                promotionRepository.save(promo);

                List<PromotionEntry> entries = entryService.getByPromotion(promo.getId());
                if (entries.isEmpty() && promo.getCustomerGroupId() != null) {
                    // Create entries if not already created (for Group targeted promotions)
                    List<Customer> customers = customerService
                            .getByAccountAndGroup(promo.getAccountId(), promo.getCustomerGroupId(), Pageable.unpaged())
                            .getContent();

                    for (Customer c : customers) {
                        PromotionEntry entry = new PromotionEntry();
                        entry.setPromotionId(promo.getId());
                        entry.setCustomerId(c.getId());
                        entry.setTriggeredDate(LocalDateTime.now());
                        entry = entryService.save(entry);

                        PromotionExecutionLog log = new PromotionExecutionLog();
                        log.setPromotionEntryId(entry.getId());
                        log.setChannel(Channel.WHATSAPP); // Default channel
                        log.setStatus(ExecutionResult.PENDING);
                        log.setResponse("Pending execution");
                        logRepository.save(log);
                    }
                } else {
                    // Entries exist, ensure ExecutionLogs exist
                    for (PromotionEntry entry : entries) {
                        List<PromotionExecutionLog> logs = logRepository.findByPromotionEntryId(entry.getId());
                        if (logs.isEmpty()) {
                            PromotionExecutionLog log = new PromotionExecutionLog();
                            log.setPromotionEntryId(entry.getId());
                            log.setChannel(Channel.WHATSAPP); // Default channel
                            log.setStatus(ExecutionResult.PENDING);
                            log.setResponse("Pending execution");
                            logRepository.save(log);
                        }
                    }
                }
            } catch (Exception e) {
                // Log exception but continue processing other scheduled promotions
                System.err.println("Error executing scheduled promotion ID: " + promo.getId() + ". " + e.getMessage());
            }
        }
    }
}
