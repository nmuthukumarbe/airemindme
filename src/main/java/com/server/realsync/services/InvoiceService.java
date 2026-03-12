package com.server.realsync.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.server.realsync.entity.Invoice;
import com.server.realsync.repo.InvoiceRepository;

@Service
public class InvoiceService {

    private final InvoiceRepository repo;

    public InvoiceService(InvoiceRepository repo) {
        this.repo = repo;
    }

    public List<Invoice> getAll() {
        return repo.findAll();
    }

    public Optional<Invoice> getById(Integer id) {
        return repo.findById(id);
    }

    public Invoice create(Invoice invoice) {
        return repo.save(invoice);
    }

    public Invoice update(Integer id, Invoice data) {
        return repo.findById(id)
                .map(existing -> {
                    existing.setAmount(data.getAmount());
                    return repo.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Invoice not found with id " + id));
    }

    public void delete(Integer id) {
        repo.deleteById(id);
    }
}
