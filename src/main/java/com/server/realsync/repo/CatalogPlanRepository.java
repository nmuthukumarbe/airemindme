package com.server.realsync.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.realsync.entity.CatalogPlan;

@Repository
public interface CatalogPlanRepository extends JpaRepository<CatalogPlan, Integer> {
 
    List<CatalogPlan> findByAccountId(Integer accountId);
 
    long countByAccountId(Integer accountId);
 
    long countByAccountIdAndStatus(Integer accountId, String status);
 
}