/**
 * 
 */
package com.server.realsync.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import com.server.realsync.entity.BusinessPlan;
import com.server.realsync.repo.BusinessPlanRepository;


/**
 * 
 */

@Service
public class BusinessPlanService {

    private final BusinessPlanRepository businessPlanRepository;

    public BusinessPlanService(BusinessPlanRepository businessPlanRepository) {
        this.businessPlanRepository = businessPlanRepository;
    }

    public BusinessPlan save(BusinessPlan plan) {
        return businessPlanRepository.save(plan);
    }

    public List<BusinessPlan> getAll() {
        return businessPlanRepository.findAll();
    }

    public List<BusinessPlan> getByAccountId(Integer accountId) {
        return businessPlanRepository.findByAccountId(accountId);
    }

    public Optional<BusinessPlan> getById(Long id) {
        return businessPlanRepository.findById(id);
    }

    public void delete(Long id) {
        businessPlanRepository.deleteById(id);
    }

}