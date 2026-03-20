package com.server.realsync.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.server.realsync.entity.CatalogProduct;
import com.server.realsync.repo.CatalogProductRepository;

@Service
public class CatalogProductService {

    @Autowired
    private CatalogProductRepository repo;

    /** All products for an account, newest first */
    public List<CatalogProduct> getByAccountId(Integer accountId) {
        return repo.findByAccountIdOrderByCreatedAtDesc(accountId);
    }

    /** Single product scoped to account */
    public Optional<CatalogProduct> getById(Integer id, Integer accountId) {
        return repo.findByIdAndAccountId(id, accountId);
    }

    /** Create or update */
    public CatalogProduct save(CatalogProduct product) {
        return repo.save(product);
    }

    /** Hard delete */
    public void delete(Integer id) {
        repo.deleteById(id);
    }

    /** Count active products */
    public long countActiveByAccountId(Integer accountId) {
        return repo.countByAccountIdAndStatus(accountId, "active");
    }

    /**
     * Toggle status: active ↔ inactive
     * Returns the updated entity, or empty if not found / not owned.
     */
    public Optional<CatalogProduct> toggleStatus(Integer id, Integer accountId) {
        return repo.findByIdAndAccountId(id, accountId).map(p -> {
            p.setStatus("active".equals(p.getStatus()) ? "inactive" : "active");
            return repo.save(p);
        });
    }
}