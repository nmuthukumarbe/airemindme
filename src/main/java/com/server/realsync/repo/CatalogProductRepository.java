package com.server.realsync.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.realsync.entity.CatalogProduct;

@Repository
public interface CatalogProductRepository extends JpaRepository<CatalogProduct, Integer> {

    List<CatalogProduct> findByAccountIdOrderByCreatedAtDesc(Integer accountId);

    Optional<CatalogProduct> findByIdAndAccountId(Integer id, Integer accountId);

    long countByAccountIdAndStatus(Integer accountId, String status);
}