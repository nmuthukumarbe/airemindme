package com.server.realsync.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.server.realsync.entity.InventoryTransaction;
import com.server.realsync.repo.InventoryTransactionRepository;

@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository repo;

    public InventoryTransactionService(
            InventoryTransactionRepository repo) {
        this.repo = repo;
    }

    public InventoryTransaction save(
            InventoryTransaction txn) {
        return repo.save(txn);
    }

    public List<InventoryTransaction> getByProduct(
            Integer productId) {
        return repo.findByProductIdOrderByCreatedAtDesc(productId);
    }
}