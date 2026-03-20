package com.server.realsync.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.server.realsync.entity.CatalogTemplate;

@Repository
public interface CatalogTemplateRepository extends JpaRepository<CatalogTemplate, Integer> {

    List<CatalogTemplate> findByAccountIdOrderByCreatedAtDesc(Integer accountId);

    Optional<CatalogTemplate> findByIdAndAccountId(Integer id, Integer accountId);

    long countByAccountIdAndStatus(Integer accountId, String status);
}