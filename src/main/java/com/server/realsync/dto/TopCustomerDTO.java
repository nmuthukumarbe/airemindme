package com.server.realsync.dto;

import java.math.BigDecimal;

public class TopCustomerDTO {
    private String customerName;
    private BigDecimal totalRevenue;

    public TopCustomerDTO() {
    }

    public TopCustomerDTO(String customerName, BigDecimal totalRevenue) {
        this.customerName = customerName;
        this.totalRevenue = totalRevenue;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }
}
