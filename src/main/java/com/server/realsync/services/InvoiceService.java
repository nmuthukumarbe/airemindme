package com.server.realsync.services;

import com.server.realsync.dto.*;
import com.server.realsync.entity.Invoice;
import com.server.realsync.entity.InvoiceStatus;
import com.server.realsync.mapper.InvoiceMapper;
import com.server.realsync.repo.CustomerRepository;
import com.server.realsync.repo.InvoiceRepository;
import com.server.realsync.spec.InvoiceSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, CustomerRepository customerRepository) {
        this.invoiceRepository = invoiceRepository;
        this.customerRepository = customerRepository;
    }

    // ==========================
    // LIST / SEARCH (summary only)
    // ==========================
    public Page<InvoiceListResponseDTO> findAll(String search, Long customerId, InvoiceStatus status,
            Pageable pageable) {
        Specification<Invoice> spec = InvoiceSpecification.filter(search, customerId, status);

        return invoiceRepository.findAll(spec, pageable)
                .map(invoice -> {
                    InvoiceListResponseDTO dto = InvoiceMapper.toListDTO(invoice);
                    if (invoice.getCustomerId() != null) {
                        customerRepository.findById(invoice.getCustomerId().intValue())
                                .ifPresent(c -> dto.setCustomerName(c.getName()));
                    }

                    return dto;
                });
    }

    // ==========================
    // GET DETAIL
    // ==========================
    public InvoiceDetailResponseDTO getById(Long id) {
        return invoiceRepository.findById(id)
                .map(InvoiceMapper::toDetailDTO)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    // ==========================
    // CREATE
    // ==========================
    public InvoiceDetailResponseDTO create(CreateInvoiceRequestDTO req) {
        // ensure uniqueness of invoice number
        Optional<Invoice> existing = invoiceRepository.findByInvoiceNumber(req.getInvoiceNumber());
        if (existing.isPresent()) {
            throw new RuntimeException("Invoice number already exists");
        }

        Invoice invoice = InvoiceMapper.toEntity(req);

        Invoice saved = invoiceRepository.save(invoice);

        return InvoiceMapper.toDetailDTO(saved);
    }

    // ==========================
    // UPDATE
    // ==========================
    public InvoiceDetailResponseDTO update(Long id, UpdateInvoiceRequestDTO dto) {
        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id " + id));

        InvoiceMapper.updateEntityFromDto(existing, dto);

        Invoice saved = invoiceRepository.save(existing);

        return InvoiceMapper.toDetailDTO(saved);
    }

    // ==========================
    // DELETE
    // ==========================
    public void delete(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id " + id));
        invoiceRepository.delete(invoice);
    }

    // ==========================
    // FIND BY INVOICE NUMBER
    // ==========================
    public Optional<Invoice> findByInvoiceNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }
}