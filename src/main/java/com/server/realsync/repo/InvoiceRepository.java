package com.server.realsync.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.server.realsync.entity.Invoice;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
}
