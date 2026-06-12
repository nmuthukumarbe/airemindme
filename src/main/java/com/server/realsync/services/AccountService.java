package com.server.realsync.services;

import com.server.realsync.entity.Account;
import com.server.realsync.repo.AccountRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccountService {

    @Autowired
    private AccountRepository repository;

    public List<Account> findAll() {
        return repository.findAll();
    }

    public Optional<Account> findById(Integer id) {
        return repository.findById(id);
    }

    public Account save(Account account) {
        return repository.save(account);
    }

    
    public Account getById(int id) {
        return repository.findById(id)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    }
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }

    @Autowired
    private com.server.realsync.repo.UserRepository userRepository;

    @Autowired
    private com.server.realsync.repo.RoleRepository roleRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @org.springframework.transaction.annotation.Transactional
    public void registerAccount(com.server.realsync.dto.SignupRequestDto dto) {
        // Validate inputs
        if (dto.getName() == null || dto.getName().trim().length() < 3) {
            throw new IllegalArgumentException("Full Name must be at least 3 characters.");
        }
        if (dto.getEmail() == null || !dto.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Please provide a valid email address.");
        }
        if (dto.getMobile() == null || !dto.getMobile().matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Mobile number must be exactly 10 digits.");
        }
        if (dto.getBusinessName() == null || dto.getBusinessName().trim().isEmpty()) {
            throw new IllegalArgumentException("Business name is required.");
        }
        if (dto.getBusinessEmail() == null || !dto.getBusinessEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Please provide a valid business email.");
        }
        if (dto.getBusinessPhone() == null || dto.getBusinessPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Business phone is required.");
        }
        
        String password = dto.getPassword();
        if (password == null || password.length() < 8 || 
            !password.matches(".*[A-Z].*") || !password.matches(".*[a-z].*") || !password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must be at least 8 characters and contain at least one uppercase letter, one lowercase letter, and one number.");
        }

        // Duplicate checks
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already registered");
        }
        if (userRepository.findByUsername(dto.getMobile()).isPresent()) {
            throw new IllegalStateException("Mobile number already registered");
        }

        // Create Account
        Account account = new Account();
        account.setName(dto.getName());
        account.setEmail(dto.getEmail());
        account.setMobile(dto.getMobile());
        account.setBusinessName(dto.getBusinessName());
        account.setBusinessEmail(dto.getBusinessEmail());
        account.setBusinessPhone(dto.getBusinessPhone());
        account.setGstNumber(dto.getGstNumber());
        account.setAddress(dto.getAddress());
        Account savedAccount = repository.save(account);

        // Fetch/Create ADMIN Role
        com.server.realsync.entity.Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> {
            com.server.realsync.entity.Role newRole = new com.server.realsync.entity.Role();
            newRole.setName("ADMIN");
            return roleRepository.save(newRole);
        });

        // Create User
        User user = new User();
        user.setUsername(dto.getMobile());
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(dto.getEmail());
        user.setFullName(dto.getName());
        user.setAccount(savedAccount);
        user.setRole(adminRole);
        userRepository.save(user);
    }
}
