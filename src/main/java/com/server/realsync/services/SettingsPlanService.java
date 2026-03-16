package com.server.realsync.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.realsync.entity.CatalogPlan;
import com.server.realsync.repo.CatalogPlanRepository;

@Service
public class SettingsPlanService {

    @Autowired
    private CatalogPlanRepository catalogPlanRepository;

    public List<CatalogPlan> getByAccountId(Integer accountId) {
        return catalogPlanRepository.findByAccountId(accountId);
    }

    public long countByAccountId(Integer accountId) {
        return catalogPlanRepository.countByAccountId(accountId);
    }

    public long countActiveByAccountId(Integer accountId) {
        return catalogPlanRepository.countByAccountIdAndStatus(accountId, "active");
    }

    public CatalogPlan save(CatalogPlan plan) {
        return catalogPlanRepository.save(plan);
    }

    public Optional<CatalogPlan> getById(Integer id) {
        return catalogPlanRepository.findById(id);
    }

    public void delete(Integer id) {
        catalogPlanRepository.deleteById(id);
    }

}