package com.server.realsync.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.server.realsync.entity.CatalogProduct;

@Repository
public interface CatalogProductRepository extends JpaRepository<CatalogProduct, Integer> {

    List<CatalogProduct> findByAccountIdOrderByCreatedAtDesc(Integer accountId);
    Page<CatalogProduct> findByAccountId(Integer accountId,Pageable pageable);

    Optional<CatalogProduct> findByIdAndAccountId(Integer id, Integer accountId);

    long countByAccountIdAndStatus(Integer accountId, String status);
    @Query("""
SELECT p
FROM CatalogProduct p
WHERE p.accountId = :accountId
AND LOWER(p.name) LIKE LOWER(CONCAT('%',:search,'%'))
""")
List<CatalogProduct> search(Integer accountId,String search);
}