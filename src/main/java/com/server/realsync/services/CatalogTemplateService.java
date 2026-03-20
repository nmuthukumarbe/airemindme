package com.server.realsync.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.realsync.entity.CatalogTemplate;
import com.server.realsync.repo.CatalogTemplateRepository;

@Service
public class CatalogTemplateService {

    @Autowired
    private CatalogTemplateRepository repo;

    /** All templates for an account, newest first */
    public List<CatalogTemplate> getByAccountId(Integer accountId) {
        return repo.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    /** Single template scoped to account */
    public Optional<CatalogTemplate> getById(Integer id, Integer accountId) {
        return repo.findByIdAndAccountId(id, accountId);
    }

    /** Create or update */
    public CatalogTemplate save(CatalogTemplate template) {
        return repo.save(template);
    }

    /** Hard delete */
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    /** Count active templates */
    public long countActiveByAccountId(Integer accountId) {
        return repo.countByAccountIdAndStatus(accountId, "active");
    }

    /**
     * Toggle status: active ↔ inactive
     * Returns the updated entity, or empty if not found / not owned.
     */
    public Optional<CatalogTemplate> toggleStatus(Integer id, Integer accountId) {
        return repo.findByIdAndAccountId(id, accountId).map(t -> {
            t.setStatus("active".equals(t.getStatus()) ? "inactive" : "active");
            return repo.save(t);
        });
    }
}