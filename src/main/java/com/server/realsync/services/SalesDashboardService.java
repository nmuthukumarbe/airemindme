package com.server.realsync.services;

import com.server.realsync.dto.SalesDashboardDTO;
import com.server.realsync.dto.MonthlyRevenueDTO;
import com.server.realsync.dto.TopCustomerDTO;
import com.server.realsync.dto.RecentInvoiceDTO;
import com.server.realsync.entity.Invoice;
import com.server.realsync.repo.InvoiceRepository;
import com.server.realsync.repo.InvoicePaymentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class SalesDashboardService {

    private final InvoiceRepository invoiceRepository;
    private final InvoicePaymentRepository paymentRepository;

    public SalesDashboardService(InvoiceRepository invoiceRepository, InvoicePaymentRepository paymentRepository) {
        this.invoiceRepository = invoiceRepository;
        this.paymentRepository = paymentRepository;
    }

    public SalesDashboardDTO getDashboardData(Integer accountId) {
        SalesDashboardDTO dto = new SalesDashboardDTO();

        // 1. Fetch aggregate metrics from InvoiceRepository
        Object metricsResult = invoiceRepository.getInvoiceDashboardMetrics(accountId);
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal outstandingAmount = BigDecimal.ZERO;
        Long totalInvoices = 0L;
        Long paidInvoices = 0L;
        Long sentInvoices = 0L;
        Long partiallyPaidInvoices = 0L;
        Long overdueInvoices = 0L;
        Long draftInvoices = 0L;
        BigDecimal overdueAmount = BigDecimal.ZERO;
        Long totalCustomersBilled = 0L;

        if (metricsResult != null) {
            Object[] row;
            if (metricsResult instanceof Object[]) {
                row = (Object[]) metricsResult;
            } else {
                row = new Object[]{ metricsResult };
            }
            if (row.length > 0) totalRevenue = toBigDecimal(row[0]);
            if (row.length > 1) outstandingAmount = toBigDecimal(row[1]);
            if (row.length > 2) totalInvoices = toLong(row[2]);
            if (row.length > 3) paidInvoices = toLong(row[3]);
            if (row.length > 4) sentInvoices = toLong(row[4]);
            if (row.length > 5) partiallyPaidInvoices = toLong(row[5]);
            if (row.length > 6) overdueInvoices = toLong(row[6]);
            if (row.length > 7) draftInvoices = toLong(row[7]);
            if (row.length > 8) overdueAmount = toBigDecimal(row[8]);
            if (row.length > 9) totalCustomersBilled = toLong(row[9]);
        }

        // 2. Fetch total collected from InvoicePaymentRepository
        Double totalCollectedDouble = paymentRepository.sumPaymentsByAccountId(accountId);
        BigDecimal totalCollected = BigDecimal.valueOf(totalCollectedDouble != null ? totalCollectedDouble : 0.0);

        // Calculate Collection Rate = (totalCollected / totalRevenue) * 100
        BigDecimal collectionRate = BigDecimal.ZERO;
        if (totalRevenue.compareTo(BigDecimal.ZERO) > 0) {
            collectionRate = totalCollected
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalRevenue, 2, RoundingMode.HALF_UP);
        }

        // Calculate Average Invoice Value = totalRevenue / totalInvoices
        BigDecimal averageInvoiceValue = BigDecimal.ZERO;
        if (totalInvoices > 0) {
            averageInvoiceValue = totalRevenue.divide(BigDecimal.valueOf(totalInvoices), 2, RoundingMode.HALF_UP);
        }

        dto.setTotalRevenue(totalRevenue);
        dto.setTotalCollected(totalCollected);
        dto.setOutstandingAmount(outstandingAmount);
        dto.setOverdueAmount(overdueAmount);
        dto.setTotalInvoices(totalInvoices);
        dto.setPaidInvoices(paidInvoices);
        // Unpaid Invoices = Sent + Partially Paid
        dto.setUnpaidInvoices(sentInvoices + partiallyPaidInvoices);
        dto.setOverdueInvoices(overdueInvoices);
        dto.setCollectionRate(collectionRate);
        dto.setAverageInvoiceValue(averageInvoiceValue);
        dto.setTotalCustomersBilled(totalCustomersBilled);

        // 3. Status Breakdown
        Map<String, Long> statusBreakdown = new HashMap<>();
        statusBreakdown.put("Draft", draftInvoices);
        statusBreakdown.put("Sent", sentInvoices);
        statusBreakdown.put("Partially Paid", partiallyPaidInvoices);
        statusBreakdown.put("Paid", paidInvoices);
        statusBreakdown.put("Overdue", overdueInvoices);
        dto.setStatusBreakdown(statusBreakdown);

        // 4. Monthly Revenue for last 12 months (inclusive of target month)
        LocalDate startDate = LocalDate.now().minusMonths(11).withDayOfMonth(1);
        List<Invoice> invoiceHistory = invoiceRepository.findInvoicesForRevenueAnalytics(accountId, startDate);
        
        List<MonthlyRevenueDTO> monthlyRevenueList = new ArrayList<>();
        LocalDate current = startDate;
        LocalDate end = LocalDate.now();
        while (!current.isAfter(end)) {
            final LocalDate targetMonth = current;
            String monthLabel = targetMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            
            BigDecimal monthlySum = invoiceHistory.stream()
                    .filter(inv -> inv.getInvoiceDate() != null && 
                                   inv.getInvoiceDate().getYear() == targetMonth.getYear() && 
                                   inv.getInvoiceDate().getMonth() == targetMonth.getMonth())
                    .map(Invoice::getGrandTotal)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            monthlyRevenueList.add(new MonthlyRevenueDTO(monthLabel, monthlySum));
            current = current.plusMonths(1);
        }
        dto.setMonthlyRevenue(monthlyRevenueList);

        // 5. Top Customers (top 5)
        List<TopCustomerDTO> topCustomers = invoiceRepository.findTop5Customers(accountId, PageRequest.of(0, 5));
        dto.setTopCustomers(topCustomers);

        // 6. Recent Invoices (latest 10)
        List<RecentInvoiceDTO> recentInvoices = invoiceRepository.findRecentInvoices(accountId, PageRequest.of(0, 10));
        dto.setRecentInvoices(recentInvoices);

        return dto;
    }

    private BigDecimal toBigDecimal(Object val) {
        if (val == null) return BigDecimal.ZERO;
        if (val instanceof BigDecimal) return (BigDecimal) val;
        try {
            return new BigDecimal(val.toString());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    private Long toLong(Object val) {
        if (val == null) return 0L;
        if (val instanceof Number) return ((Number) val).longValue();
        try {
            return Long.parseLong(val.toString());
        } catch (NumberFormatException e) {
            return 0L;
        }
    }
}
