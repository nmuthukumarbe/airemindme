package com.server.realsync.services;

import com.server.realsync.entity.InvoicePayment;
import com.server.realsync.repo.InvoicePaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvoicePaymentService {

    private final InvoicePaymentRepository repository;

    public InvoicePaymentService(InvoicePaymentRepository repository) {
        this.repository = repository;
    }

    public InvoicePayment save(InvoicePayment payment) {
        return repository.save(payment);
    }

    public List<InvoicePayment> getByInvoice(Integer invoiceId) {
        return repository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId);
    }

    public Double getTotalPaid(Integer invoiceId) {
        return repository.getTotalPaid(invoiceId);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}