package com.server.realsync.controllers;

import com.server.realsync.entity.Invoice;
import com.server.realsync.services.InvoiceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(
            InvoiceService invoiceService) {

        this.invoiceService = invoiceService;
    }

    @GetMapping
    public List<Invoice> getAll() {
        return invoiceService.getAll();
    }

    @GetMapping("/{id}")
    public Invoice getById(
            @PathVariable Long id) {

        return invoiceService.getById(id)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found"));
    }

    @PostMapping
    public Invoice create(
            @RequestBody Invoice invoice) {

        return invoiceService.create(invoice);
    }

    @PutMapping("/{id}")
    public Invoice update(
            @PathVariable Long id,
            @RequestBody Invoice invoice) {

        return invoiceService.update(id, invoice);
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id) {

        invoiceService.delete(id);
    }
}