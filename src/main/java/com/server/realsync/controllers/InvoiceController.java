package com.server.realsync.controllers;

import com.server.realsync.dto.*;
import com.server.realsync.entity.InvoiceStatus;
import com.server.realsync.services.InvoiceService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public Page<InvoiceListResponseDTO> list(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return invoiceService.findAll(search, customerId, status, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }

    @GetMapping("/{id}")
    public InvoiceDetailResponseDTO getById(@PathVariable Long id) {
        return invoiceService.getById(id);
    }

    @PostMapping
    public ResponseEntity<InvoiceDetailResponseDTO> create(@Valid @RequestBody CreateInvoiceRequestDTO req) {
        InvoiceDetailResponseDTO saved = invoiceService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping("/{id}")
    public InvoiceDetailResponseDTO update(@PathVariable Long id, @Valid @RequestBody UpdateInvoiceRequestDTO req) {
        return invoiceService.update(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        invoiceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}