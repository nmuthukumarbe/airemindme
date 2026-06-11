package com.server.realsync.repo;

import com.server.realsync.entity.PromotionItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PromotionItemRepository extends JpaRepository<PromotionItem, Long> {
    List<PromotionItem> findByPromotionId(Long promotionId);
    void deleteByPromotionId(Long promotionId);
}
