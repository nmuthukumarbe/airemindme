package com.server.realsync.dto;

import java.math.BigDecimal;

public class InvoicePaymentDashboardDTO {

    private BigDecimal totalCollected;
    private BigDecimal pendingReceivables;
    private BigDecimal partiallyPaidAmount;
    private BigDecimal overdueAmount;

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public BigDecimal getPendingReceivables() {
        return pendingReceivables;
    }

    public void setPendingReceivables(BigDecimal pendingReceivables) {
        this.pendingReceivables = pendingReceivables;
    }

    public BigDecimal getPartiallyPaidAmount() {
        return partiallyPaidAmount;
    }

    public void setPartiallyPaidAmount(BigDecimal partiallyPaidAmount) {
        this.partiallyPaidAmount = partiallyPaidAmount;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }
}