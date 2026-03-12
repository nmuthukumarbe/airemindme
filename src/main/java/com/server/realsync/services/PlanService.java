/**
 * 
 */
package com.server.realsync.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.realsync.entity.Plan;
import com.server.realsync.repo.PlanRepository;


/**
 * 
 */

@Service
public class PlanService {

    @Autowired
    private PlanRepository planRepository;

    public Plan savePlan(Plan plan) {
        return planRepository.save(plan);
    }

    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    public Optional<Plan> findById(Integer id) {
        return planRepository.findById(id);
    }

    public void deletePlan(Integer id) {
        planRepository.deleteById(id);
    }
}
