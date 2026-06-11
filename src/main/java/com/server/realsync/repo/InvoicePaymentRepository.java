package com.server.realsync.repo;

import com.server.realsync.entity.InvoicePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
public interface InvoicePaymentRepository extends JpaRepository<InvoicePayment, Integer> {

    List<InvoicePayment> findByInvoiceIdOrderByPaymentDateDesc(Integer invoiceId);

    List<InvoicePayment> findByAccountId(Integer accountId);

    List<InvoicePayment> findByAccountIdOrderByPaymentDateDesc(
            Integer accountId);

    List<InvoicePayment> findByAccountIdAndInvoiceId(
            Integer accountId,
            Integer invoiceId);

    @Query("""
            SELECT COALESCE(SUM(p.amount),0)
            FROM InvoicePayment p
            WHERE p.invoiceId = :invoiceId
            """)
    Double getTotalPaid(Integer invoiceId);
}