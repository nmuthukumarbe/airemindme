package com.server.realsync.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class SalesDashboardDTO {
    private BigDecimal totalRevenue;
    private BigDecimal totalCollected;
    private BigDecimal outstandingAmount;
    private BigDecimal overdueAmount;
    private Long totalInvoices;
    private Long paidInvoices;
    private Long unpaidInvoices;
    private Long overdueInvoices;
    private BigDecimal collectionRate;
    private BigDecimal averageInvoiceValue;
    private Long totalCustomersBilled;

    private List<MonthlyRevenueDTO> monthlyRevenue;
    private List<TopCustomerDTO> topCustomers;
    private Map<String, Long> statusBreakdown;
    private List<RecentInvoiceDTO> recentInvoices;

    public SalesDashboardDTO() {
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(BigDecimal totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCollected() {
        return totalCollected;
    }

    public void setTotalCollected(BigDecimal totalCollected) {
        this.totalCollected = totalCollected;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public BigDecimal getOverdueAmount() {
        return overdueAmount;
    }

    public void setOverdueAmount(BigDecimal overdueAmount) {
        this.overdueAmount = overdueAmount;
    }

    public Long getTotalInvoices() {
        return totalInvoices;
    }

    public void setTotalInvoices(Long totalInvoices) {
        this.totalInvoices = totalInvoices;
    }

    public Long getPaidInvoices() {
        return paidInvoices;
    }

    public void setPaidInvoices(Long paidInvoices) {
        this.paidInvoices = paidInvoices;
    }

    public Long getUnpaidInvoices() {
        return unpaidInvoices;
    }

    public void setUnpaidInvoices(Long unpaidInvoices) {
        this.unpaidInvoices = unpaidInvoices;
    }

    public Long getOverdueInvoices() {
        return overdueInvoices;
    }

    public void setOverdueInvoices(Long overdueInvoices) {
        this.overdueInvoices = overdueInvoices;
    }

    public BigDecimal getCollectionRate() {
        return collectionRate;
    }

    public void setCollectionRate(BigDecimal collectionRate) {
        this.collectionRate = collectionRate;
    }

    public BigDecimal getAverageInvoiceValue() {
        return averageInvoiceValue;
    }

    public void setAverageInvoiceValue(BigDecimal averageInvoiceValue) {
        this.averageInvoiceValue = averageInvoiceValue;
    }

    public Long getTotalCustomersBilled() {
        return totalCustomersBilled;
    }

    public void setTotalCustomersBilled(Long totalCustomersBilled) {
        this.totalCustomersBilled = totalCustomersBilled;
    }

    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(List<MonthlyRevenueDTO> monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public List<TopCustomerDTO> getTopCustomers() {
        return topCustomers;
    }

    public void setTopCustomers(List<TopCustomerDTO> topCustomers) {
        this.topCustomers = topCustomers;
    }

    public Map<String, Long> getStatusBreakdown() {
        return statusBreakdown;
    }

    public void setStatusBreakdown(Map<String, Long> statusBreakdown) {
        this.statusBreakdown = statusBreakdown;
    }

    public List<RecentInvoiceDTO> getRecentInvoices() {
        return recentInvoices;
    }

    public void setRecentInvoices(List<RecentInvoiceDTO> recentInvoices) {
        this.recentInvoices = recentInvoices;
    }
}
