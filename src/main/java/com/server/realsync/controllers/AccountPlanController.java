package com.server.realsync.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.realsync.dto.AccountPlanUsageDto;
import com.server.realsync.entity.AccountPlan;
import com.server.realsync.services.AccountPlanService;

@RestController
@RequestMapping("/api/account-plans")
public class AccountPlanController {

    @Autowired
    private AccountPlanService service;

    @GetMapping
    public ResponseEntity<List<AccountPlan>> getAllAccountPlans() {
        List<AccountPlan> accountPlans = service.findAll();
        return new ResponseEntity<>(accountPlans, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccountPlan> getAccountPlanById(@PathVariable Integer id) {
        Optional<AccountPlan> accountPlan = service.findById(id);
        return accountPlan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccountPlan> createAccountPlan( @RequestBody AccountPlan accountPlan, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        AccountPlan savedAccountPlan = service.save(accountPlan);
        return new ResponseEntity<>(savedAccountPlan, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccountPlan> updateAccountPlan(@PathVariable Integer id, @RequestBody AccountPlan accountPlan, BindingResult result) {
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(null);
        }
        if (service.findById(id).isPresent()) {
            accountPlan.setId(id);
            AccountPlan updatedAccountPlan = service.save(accountPlan);
            return new ResponseEntity<>(updatedAccountPlan, HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccountPlan(@PathVariable Integer id) {
        if (service.findById(id).isPresent()) {
            service.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/{accountId}/usage")
    public ResponseEntity<AccountPlan> getAccountPlanUsage(@PathVariable Integer accountId) {
        return service.getAccountPlanUsage(accountId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}