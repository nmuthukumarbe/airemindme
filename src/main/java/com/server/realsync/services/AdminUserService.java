package com.server.realsync.services;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.server.realsync.entity.AdminUser;
import com.server.realsync.repo.AdminUserRepository;

@Service
public class AdminUserService {

    private final AdminUserRepository repo;

    public AdminUserService(AdminUserRepository repo) {
        this.repo = repo;
    }

    public AdminUser save(AdminUser user) {
        return repo.save(user);
    }

    public Page<AdminUser> getByAccount(Integer accountId, Pageable pageable) {
        return repo.findByAccountId(accountId, pageable);
    }

    public Optional<AdminUser> getById(Integer accountId, Integer id) {
        return repo.findByIdAndAccountId(id, accountId);
    }

    public Optional<AdminUser> findByEmail(Integer accountId, String email) {
        return repo.findByAccountIdAndEmail(accountId, email);
    }

    public long totalUsers(Integer accountId) {
        return repo.countByAccountId(accountId);
    }

    public long totalAdmins(Integer accountId) {
        return repo.countByAccountIdAndRole(accountId, "Admin");
    }

    public long activeUsers(Integer accountId) {
        return repo.countByAccountIdAndStatus(accountId, "Active");
    }

    public long inactiveUsers(Integer accountId) {
        return repo.countByAccountIdAndStatus(accountId, "Inactive");
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}