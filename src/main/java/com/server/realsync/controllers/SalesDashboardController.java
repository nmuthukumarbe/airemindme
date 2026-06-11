package com.server.realsync.controllers;

import com.server.realsync.dto.SalesDashboardDTO;
import com.server.realsync.services.SalesDashboardService;
import com.server.realsync.util.SecurityUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales")
public class SalesDashboardController {

    private final SalesDashboardService dashboardService;

    public SalesDashboardController(SalesDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<SalesDashboardDTO> getDashboard() {
        Integer accountId = SecurityUtil.getCurrentAccountId().getId();
        SalesDashboardDTO dto = dashboardService.getDashboardData(accountId);
        return ResponseEntity.ok(dto);
    }
}
