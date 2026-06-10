package com.server.realsync.services;

import com.server.realsync.entity.Invoice;
import com.server.realsync.entity.InvoiceItem;
import com.server.realsync.repo.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;

    public InvoiceService(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    // ==========================
    // GET ALL INVOICES
    // ==========================

    public List<Invoice> getAll() {
        return invoiceRepository.findAll();
    }

    // ==========================
    // GET BY ID
    // ==========================

    public Optional<Invoice> getById(Long id) {
        return invoiceRepository.findById(id);
    }

    // ==========================
    // CREATE INVOICE
    // ==========================

    public Invoice create(Invoice invoice) {

        if (invoice.getItems() != null) {

            for (InvoiceItem item : invoice.getItems()) {
                item.setInvoice(invoice);
            }

        }

        return invoiceRepository.save(invoice);
    }

    // ==========================
    // UPDATE INVOICE
    // ==========================

    public Invoice update(Long id, Invoice data) {

        return invoiceRepository.findById(id)
                .map(existing -> {

                    existing.setInvoiceNumber(data.getInvoiceNumber());
                    existing.setCustomerId(data.getCustomerId());

                    existing.setInvoiceDate(data.getInvoiceDate());
                    existing.setDueDate(data.getDueDate());

                    existing.setSubtotal(data.getSubtotal());
                    existing.setTaxAmount(data.getTaxAmount());

                    existing.setDiscountAmount(data.getDiscountAmount());
                    existing.setShippingAmount(data.getShippingAmount());

                    existing.setGrandTotal(data.getGrandTotal());

                    existing.setStatus(data.getStatus());

                    existing.setNotes(data.getNotes());
                    existing.setTerms(data.getTerms());

                    existing.getItems().clear();

                    if (data.getItems() != null) {

                        for (InvoiceItem item : data.getItems()) {

                            item.setInvoice(existing);

                            existing.getItems().add(item);
                        }
                    }

                    return invoiceRepository.save(existing);
                })
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found with id " + id));
    }

    // ==========================
    // DELETE INVOICE
    // ==========================

    public void delete(Long id) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found with id " + id));

        invoiceRepository.delete(invoice);
    }

    // ==========================
    // FIND BY INVOICE NUMBER
    // ==========================

    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

}