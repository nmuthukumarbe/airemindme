package com.server.realsync.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.server.realsync.dto.CustomerImportDto;
import com.server.realsync.dto.ImportSummaryDto;
import com.server.realsync.entity.Account;
import com.server.realsync.services.CustomerImportService;
import com.server.realsync.util.SecurityUtil;

@RestController
@RequestMapping("/api/customers/import")
public class CustomerImportController {

    private final CustomerImportService customerImportService;

    public CustomerImportController(CustomerImportService customerImportService) {
        this.customerImportService = customerImportService;
    }

    @PostMapping("/validate")
    public ResponseEntity<ImportSummaryDto> validateImport(@RequestBody List<CustomerImportDto> rows) {
        Account account = SecurityUtil.getCurrentAccountId();
        ImportSummaryDto summary = customerImportService.validateImport(account.getId(), rows);
        return ResponseEntity.ok(summary);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> importCustomers(@RequestBody List<CustomerImportDto> rows) {
        Account account = SecurityUtil.getCurrentAccountId();
        Map<String, Object> result = customerImportService.importCustomers(account.getId(), rows);
        return ResponseEntity.ok(result);
    }
}
