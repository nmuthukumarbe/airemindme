package com.server.realsync.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.server.realsync.entity.CatalogPlan;
import com.server.realsync.entity.CatalogProduct;

@Repository
public interface CatalogPlanRepository extends JpaRepository<CatalogPlan, Integer> {

    List<CatalogPlan> findByAccountId(Integer accountId);

    Page<CatalogPlan> findByAccountId(Integer accountId, Pageable pageable);

    long countByAccountId(Integer accountId);

    long countByAccountIdAndStatus(Integer accountId, String status);

    @Query("""
            SELECT p
            FROM CatalogPlan p
            WHERE p.accountId = :accountId
            AND LOWER(p.name) LIKE LOWER(CONCAT('%',:search,'%'))
            """)
    List<CatalogPlan> search(Integer accountId,String search);

}