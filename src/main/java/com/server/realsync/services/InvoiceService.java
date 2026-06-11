package com.server.realsync.services;

import com.server.realsync.dto.*;
import com.server.realsync.entity.CatalogProduct;
import com.server.realsync.entity.InventoryTransaction;
import com.server.realsync.entity.Invoice;
import com.server.realsync.entity.InvoiceItem;
import com.server.realsync.entity.InvoiceStatus;
import com.server.realsync.mapper.InvoiceMapper;
import com.server.realsync.repo.CustomerRepository;
import com.server.realsync.repo.InvoiceRepository;
import com.server.realsync.spec.InvoiceSpecification;
import com.server.realsync.util.SecurityUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    private InventoryTransactionService txnService;
    @Autowired
    private CatalogProductService productService;

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

    public Optional<Invoice> findEntityById(Long id) {
        return invoiceRepository.findById(id);
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
        invoice.setCustomerName(req.getCustomerName());
        invoice.setCustomerAddress(req.getCustomerAddress());
        invoice.setCustomerPhone(req.getCustomerPhone());
        invoice.setCustomerGst(req.getCustomerGst());
        invoice.setShippingAddress(req.getShippingAddress());
        Invoice saved = invoiceRepository.save(invoice);

        if (saved.getStatus() != InvoiceStatus.DRAFT
                && !Boolean.TRUE.equals(saved.getInventoryProcessed())) {

            processInventory(saved);

            saved.setInventoryProcessed(true);

            saved = invoiceRepository.save(saved);
        }

        return InvoiceMapper.toDetailDTO(saved);

    }

    // ==========================
    // UPDATE
    // ==========================
    public InvoiceDetailResponseDTO update(Long id, UpdateInvoiceRequestDTO dto) {
        Invoice existing = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id " + id));

        InvoiceMapper.updateEntityFromDto(existing, dto);
        existing.setCustomerName(dto.getCustomerName());
        existing.setCustomerAddress(dto.getCustomerAddress());
        existing.setCustomerPhone(dto.getCustomerPhone());
        existing.setCustomerGst(dto.getCustomerGst());
        existing.setShippingAddress(dto.getShippingAddress());
        Invoice saved = invoiceRepository.save(existing);

        if (saved.getStatus() != InvoiceStatus.DRAFT
                && !Boolean.TRUE.equals(saved.getInventoryProcessed())) {

            processInventory(saved);

            saved.setInventoryProcessed(true);

            saved = invoiceRepository.save(saved);
        }

        return InvoiceMapper.toDetailDTO(saved);
    }

    private void processInventory(Invoice invoice) {

        for (InvoiceItem item : invoice.getItems()) {

            if (!"PRODUCT".equalsIgnoreCase(item.getItemType())) {
                continue;
            }

            if (item.getItemRefId() == null) {
                continue;
            }

            CatalogProduct product = productService.getById(
                    item.getItemRefId().intValue(),
                    SecurityUtil.getCurrentAccountId().getId()).orElse(null);

            if (product == null) {
                continue;
            }

            int currentQty = product.getQuantity() == null
                    ? 0
                    : product.getQuantity();

            int soldQty = item.getQty() == null
                    ? 0
                    : item.getQty();

            if (soldQty > currentQty) {
                throw new RuntimeException(
                        "Insufficient stock for " + product.getName());
            }

            int newQty = currentQty - soldQty;

            product.setQuantity(newQty);

            productService.save(product);

            InventoryTransaction txn = new InventoryTransaction();

            txn.setAccountId(product.getAccountId());

            txn.setProductId(product.getId());

            txn.setType("SALE");

            txn.setQuantity(-soldQty);

            txn.setBalanceAfter(newQty);

            txn.setReferenceNo(
                    invoice.getInvoiceNumber());

            txn.setNotes(
                    "Sold via Invoice " +
                            invoice.getInvoiceNumber());

            txnService.save(txn);
        }
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