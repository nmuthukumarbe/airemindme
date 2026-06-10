package com.server.realsync.spec;

import com.server.realsync.entity.Invoice;
import com.server.realsync.entity.InvoiceStatus;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;

public class InvoiceSpecification {

    public static Specification<Invoice> filter(String search, Long customerId, InvoiceStatus status) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (search != null && !search.isBlank()) {
                String like = "%" + search.trim().toUpperCase() + "%";
                p = cb.and(p, cb.like(cb.upper(root.get("invoiceNumber")), like));
            }

            if (customerId != null) {
                p = cb.and(p, cb.equal(root.get("customerId"), customerId));
            }

            if (status != null) {
                p = cb.and(p, cb.equal(root.get("status"), status));
            }

            return p;
        };
    }
}
