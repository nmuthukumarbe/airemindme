/**
 * 
 */
package com.server.realsync.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.server.realsync.entity.BusinessPlan;

/**
 * 
 */

public interface BusinessPlanRepository extends JpaRepository<BusinessPlan, Long> {

    List<BusinessPlan> findByAccountId(Integer accountId);

}