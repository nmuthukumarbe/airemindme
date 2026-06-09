package com.server.realsync.services;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.realsync.entity.CatalogPlan;
import com.server.realsync.repo.CatalogPlanRepository;

@Service
public class CatlogPlanService {

    @Autowired
    private CatalogPlanRepository catalogPlanRepository;

    public List<CatalogPlan> getByAccountId(Integer accountId) {
        return catalogPlanRepository.findByAccountId(accountId);
    }

    public Page<CatalogPlan> getByAccountId(Integer accountId, Pageable pageable) {
        return catalogPlanRepository.findByAccountId(accountId, pageable);
    }

    public List<CatalogPlan> search(Integer accountId, String query) {
        return catalogPlanRepository.search(accountId, query);
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