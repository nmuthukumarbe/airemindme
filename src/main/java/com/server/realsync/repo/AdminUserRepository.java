package com.server.realsync.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.server.realsync.entity.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {

    Page<AdminUser> findByAccountId(Integer accountId, Pageable pageable);

    Optional<AdminUser> findByIdAndAccountId(Integer id, Integer accountId);

    Optional<AdminUser> findByAccountIdAndEmail(Integer accountId, String email);

    long countByAccountId(Integer accountId);

    long countByAccountIdAndRole(Integer accountId, String role);

    long countByAccountIdAndStatus(Integer accountId, String status);

}