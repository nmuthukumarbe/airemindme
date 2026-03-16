package com.server.realsync.mvc.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.server.realsync.entity.Account;
import com.server.realsync.entity.CatalogPlan;
import com.server.realsync.services.SettingsPlanService;
import com.server.realsync.util.SecurityUtil;

@RestController
@RequestMapping("/api/catalog")
public class CatlogContoller {

    @Autowired
    private SettingsPlanService settingsPlanService;

    // GET /api/catalog/plans
    @GetMapping("/plans")
    public List<CatalogPlan> getPlans() {
        Account account = SecurityUtil.getCurrentAccountId();
        return settingsPlanService.getByAccountId(account.getId());
    }

    // POST /api/catalog/plans
    @PostMapping("/plans")
    public CatalogPlan createPlan(@RequestBody CatalogPlan plan) {
        Account account = SecurityUtil.getCurrentAccountId();
        plan.setAccountId(account.getId());
        return settingsPlanService.save(plan);
    }

    // PUT /api/catalog/plans/{id}
    @PutMapping("/plans/{id}")
    public ResponseEntity<CatalogPlan> updatePlan(@PathVariable Integer id, @RequestBody CatalogPlan plan) {
        Account account = SecurityUtil.getCurrentAccountId();
        return settingsPlanService.getById(id)
            .filter(existing -> existing.getAccountId().equals(account.getId()))
            .map(existing -> {
                plan.setId(id);
                plan.setAccountId(account.getId());
                return ResponseEntity.ok(settingsPlanService.save(plan));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/catalog/plans/{id}
    @DeleteMapping("/plans/{id}")
    public ResponseEntity<Void> deletePlan(@PathVariable Integer id) {
        Account account = SecurityUtil.getCurrentAccountId();
        return settingsPlanService.getById(id)
            .filter(existing -> existing.getAccountId().equals(account.getId()))
            .map(existing -> {
                settingsPlanService.delete(id);
                return ResponseEntity.ok().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

}