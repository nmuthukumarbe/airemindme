package com.server.realsync.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.realsync.entity.InventoryTransaction;

public interface InventoryTransactionRepository
        extends JpaRepository<InventoryTransaction, Integer> {

    List<InventoryTransaction>
    findByProductIdOrderByCreatedAtDesc(Integer productId);
}