/**
 * 
 */
package com.server.realsync.services;

import java.util.List;
import org.springframework.stereotype.Service;
import com.server.realsync.entity.PromotionExecutionLog;
import com.server.realsync.repo.PromotionExecutionLogRepository;

/**
 * 
 */

@Service
public class PromotionExecutionLogService {

    private final PromotionExecutionLogRepository repository;

    public PromotionExecutionLogService(PromotionExecutionLogRepository repository) {
        this.repository = repository;
    }

    public PromotionExecutionLog save(PromotionExecutionLog log) {
        return repository.save(log);
    }

    public List<PromotionExecutionLog> getByEntry(Long promotionEntryId) {
        return repository.findByPromotionEntryId(promotionEntryId);
    }
}