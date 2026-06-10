package com.server.realsync.entity;

/**
 * Invoice lifecycle status values.
 */
public enum InvoiceStatus {
    DRAFT,
    SENT,
    PARTIALLY_PAID,
    PAID,
    OVERDUE,
    CANCELLED
}
