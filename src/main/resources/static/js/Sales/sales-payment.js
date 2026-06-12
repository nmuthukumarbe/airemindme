let allPayments = [];
let allInvoicesData = [];
let filteredPayments = [];

let payPage = 1;
let payPageSize = 10;

// Filters state
let paySearchQuery = "";
let payDatePreset = "all";
let payDateFrom = "";
let payDateTo = "";
let payModeFilter = "all";
let payStatusFilter = "all";
let payAmountFilter = "all";
let payCustomerFilter = "";
let payInvoiceFilter = "";

document.addEventListener('DOMContentLoaded', () => {
    loadPaymentsPageData();
});

async function loadPaymentsPageData() {
    console.log("Loading payments and invoices data...");
    try {
        // Fetch payments
        const payRes = await fetch('/api/payments/list');
        allPayments = await payRes.json();
        
        // Fetch all invoices for metrics calculations
        const invRes = await fetch('/api/invoices?size=10000');
        const invPage = await invRes.json();
        allInvoicesData = invPage.content || [];
        
        console.log(`Loaded ${allPayments.length} payments and ${allInvoicesData.length} invoices.`);
        
        calculateMetrics();
        filterPayments();
    } catch (e) {
        console.error("Error loading payments page data:", e);
        const paymentsList = document.getElementById('payTableBody');
        if (paymentsList) {
            paymentsList.innerHTML = `
                <tr>
                    <td colspan="10" class="p-8 text-center text-red-500">
                        Failed to load payment history. Please try again.
                    </td>
                </tr>
            `;
        }
    }
}

function calculateMetrics() {
    // 1. Total Payments
    const totalPaymentsCount = allPayments.length;
    document.getElementById('stat-total-payments').textContent = totalPaymentsCount;
    
    // 2. Total Collected
    const totalCollected = allPayments.reduce((sum, p) => sum + (p.amount || 0), 0);
    document.getElementById('stat-total-collected').textContent = formatCurrency(totalCollected);
    
    // 3. Average Payment Value
    const avgPayment = totalPaymentsCount > 0 ? (totalCollected / totalPaymentsCount) : 0;
    document.getElementById('stat-avg-payment').textContent = formatCurrency(avgPayment);
    
    // 4. Highest Payment
    const highestPayment = allPayments.reduce((max, p) => Math.max(max, p.amount || 0), 0);
    document.getElementById('stat-highest-payment').textContent = formatCurrency(highestPayment);
    
    // 5. Overdue Invoices Count & Overdue Amount
    const today = new Date();
    today.setHours(0,0,0,0);
    
    let overdueCount = 0;
    let overdueBalance = 0;
    let pendingBalance = 0;
    let partialBalance = 0;
    let totalRevenue = 0;
    
    allInvoicesData.forEach(inv => {
        const grandTotal = inv.grandTotal || 0;
        const balance = inv.balanceAmount || 0;
        const status = (inv.status || 'DRAFT').toUpperCase();
        
        if (status !== 'CANCELLED') {
            totalRevenue += grandTotal;
        }
        
        if (status === 'OVERDUE') {
            overdueCount++;
            overdueBalance += balance;
        } else if (status !== 'PAID' && status !== 'CANCELLED' && status !== 'DRAFT') {
            // Check if past due date dynamically
            if (inv.dueDate) {
                const dueDate = new Date(inv.dueDate);
                if (dueDate < today) {
                    overdueCount++;
                    overdueBalance += balance;
                }
            }
        }
        
        if (status === 'SENT' || status === 'UNPAID') {
            pendingBalance += balance;
        }
        
        if (status === 'PARTIALLY_PAID') {
            partialBalance += balance;
        }
    });
    
    document.getElementById('stat-overdue-count').textContent = overdueCount;
    
    // 6. Collection Rate %
    const collectionRate = totalRevenue > 0 ? ((totalCollected / totalRevenue) * 100) : 0;
    document.getElementById('stat-collection-rate').textContent = collectionRate.toFixed(1) + '%';
}

function handlePayDatePreset(val) {
    payDatePreset = val;
    const customContainer = document.getElementById('payCustomDateRange');
    if (val === 'custom') {
        customContainer.classList.remove('hidden');
    } else {
        customContainer.classList.add('hidden');
        document.getElementById('payDateFrom').value = "";
        document.getElementById('payDateTo').value = "";
        payDateFrom = "";
        payDateTo = "";
    }
    filterPayments();
}

function filterPayments() {
    payPage = 1;
    paySearchQuery = (document.getElementById('paySearch').value || '').toLowerCase().trim();
    payModeFilter = document.getElementById('payModeFilter').value;
    payStatusFilter = document.getElementById('payStatusFilter').value;
    payAmountFilter = document.getElementById('payAmountFilter').value;
    payCustomerFilter = (document.getElementById('payCustomerFilter').value || '').toLowerCase().trim();
    payInvoiceFilter = (document.getElementById('payInvoiceFilter').value || '').toLowerCase().trim();
    payDateFrom = document.getElementById('payDateFrom').value;
    payDateTo = document.getElementById('payDateTo').value;

    filteredPayments = allPayments.filter(p => {
        // Global Search
        if (paySearchQuery) {
            const matchesSearch = 
                String(p.customerName || '').toLowerCase().includes(paySearchQuery) ||
                String(p.invoiceNumber || '').toLowerCase().includes(paySearchQuery) ||
                String(p.referenceNo || '').toLowerCase().includes(paySearchQuery) ||
                String(p.notes || '').toLowerCase().includes(paySearchQuery) ||
                String(p.paymentMode || '').toLowerCase().includes(paySearchQuery);
            if (!matchesSearch) return false;
        }
        
        // Date Presets
        if (payDatePreset !== 'all' && p.paymentDate) {
            const pDate = new Date(p.paymentDate);
            pDate.setHours(0,0,0,0);
            const now = new Date();
            now.setHours(0,0,0,0);
            
            if (payDatePreset === 'today') {
                if (pDate.getTime() !== now.getTime()) return false;
            } else if (payDatePreset === 'last7') {
                const diffTime = Math.abs(now - pDate);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                if (diffDays > 7) return false;
            } else if (payDatePreset === 'last30') {
                const diffTime = Math.abs(now - pDate);
                const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
                if (diffDays > 30) return false;
            } else if (payDatePreset === 'thismonth') {
                if (pDate.getMonth() !== now.getMonth() || pDate.getFullYear() !== now.getFullYear()) return false;
            }
        }
        
        // Custom Dates
        if (payDatePreset === 'custom') {
            if (payDateFrom) {
                const fromDate = new Date(payDateFrom);
                fromDate.setHours(0,0,0,0);
                const pDate = new Date(p.paymentDate);
                pDate.setHours(0,0,0,0);
                if (pDate < fromDate) return false;
            }
            if (payDateTo) {
                const toDate = new Date(payDateTo);
                toDate.setHours(0,0,0,0);
                const pDate = new Date(p.paymentDate);
                pDate.setHours(0,0,0,0);
                if (pDate > toDate) return false;
            }
        }
        
        // Payment Mode
        if (payModeFilter !== 'all' && String(p.paymentMode || '').toUpperCase() !== payModeFilter.toUpperCase()) {
            return false;
        }
        
        // Invoice Status
        if (payStatusFilter !== 'all' && String(p.status || '').toUpperCase() !== payStatusFilter.toUpperCase()) {
            return false;
        }
        
        // Amount Filter
        if (payAmountFilter !== 'all') {
            const amt = p.amount || 0;
            if (payAmountFilter === '0-500' && (amt < 0 || amt > 500)) return false;
            if (payAmountFilter === '500-1000' && (amt < 500 || amt > 1000)) return false;
            if (payAmountFilter === '1000-5000' && (amt < 1000 || amt > 5000)) return false;
            if (payAmountFilter === '5000+' && amt < 5000) return false;
        }
        
        // Customer Name specific filter
        if (payCustomerFilter && !String(p.customerName || '').toLowerCase().includes(payCustomerFilter)) {
            return false;
        }
        
        // Invoice Number specific filter
        if (payInvoiceFilter && !String(p.invoiceNumber || '').toLowerCase().includes(payInvoiceFilter)) {
            return false;
        }
        
        return true;
    });
    
    renderPaymentsTable();
}

function changePayPageSize(val) {
    payPageSize = parseInt(val);
    payPage = 1;
    renderPaymentsTable();
}

function renderPaymentsTable() {
    const tbody = document.getElementById('payTableBody');
    const mobileList = document.getElementById('payMobileList');
    const noMsg = document.getElementById('noPaymentsMsg');
    
    const startIdx = (payPage - 1) * payPageSize;
    const endIdx = startIdx + payPageSize;
    const paginated = filteredPayments.slice(startIdx, endIdx);
    
    if (filteredPayments.length === 0) {
        tbody.innerHTML = "";
        mobileList.innerHTML = "";
        noMsg.classList.remove('hidden');
        document.getElementById('payPagination').innerHTML = "";
        return;
    }
    
    noMsg.classList.add('hidden');
    
    // Desktop table rendering
    tbody.innerHTML = paginated.map(p => {
        const dateStr = p.paymentDate || '-';
        const invNum = p.invoiceNumber || '-';
        const custName = p.customerName || '-';
        const mode = p.paymentMode || '-';
        const refNo = p.referenceNo || '-';
        const invAmt = p.invoiceAmount !== undefined ? formatCurrency(p.invoiceAmount) : '-';
        const paidAmt = formatCurrency(p.amount);
        const balance = p.remainingBalance !== undefined ? formatCurrency(p.remainingBalance) : '-';
        const status = p.status || 'Pending';
        
        const badgeColor = getStatusBadgeColor(status);
        
        return `
            <tr class="hover:bg-gray-50 cursor-pointer text-sm" onclick="showPaymentDetails(${p.id})">
                <td class="px-5 py-3.5 font-medium text-gray-800">${dateStr}</td>
                <td class="px-4 py-3.5 font-semibold text-indigo-600">${invNum}</td>
                <td class="px-4 py-3.5 text-gray-700">${custName}</td>
                <td class="px-4 py-3.5 text-gray-500">${mode}</td>
                <td class="px-4 py-3.5 text-gray-500 font-mono text-xs">${refNo}</td>
                <td class="px-4 py-3.5 text-gray-700 text-right">${invAmt}</td>
                <td class="px-4 py-3.5 text-green-600 font-bold text-right">${paidAmt}</td>
                <td class="px-4 py-3.5 text-gray-700 text-right">${balance}</td>
                <td class="px-4 py-3.5 text-center">
                    <span class="text-xs font-semibold px-2.5 py-1 rounded-full ${badgeColor}">
                        ${status.replace('_', ' ')}
                    </span>
                </td>
                <td class="px-4 py-3.5 text-center" onclick="event.stopPropagation()">
                    <button onclick="showPaymentDetails(${p.id})" class="text-xs px-2.5 py-1 rounded-lg border border-gray-200 text-indigo-600 hover:bg-indigo-50 font-medium">View</button>
                </td>
            </tr>
        `;
    }).join('');
    
    // Mobile cards rendering
    mobileList.innerHTML = paginated.map(p => {
        const dateStr = p.paymentDate || '-';
        const invNum = p.invoiceNumber || '-';
        const custName = p.customerName || '-';
        const paidAmt = formatCurrency(p.amount);
        const status = p.status || 'Pending';
        const badgeColor = getStatusBadgeColor(status);
        
        return `
            <div class="p-4 space-y-2 cursor-pointer hover:bg-gray-50" onclick="showPaymentDetails(${p.id})">
                <div class="flex justify-between items-start">
                    <div>
                        <p class="font-semibold text-gray-900">${custName}</p>
                        <p class="text-xs text-gray-400">${invNum} · ${dateStr}</p>
                    </div>
                    <span class="text-xs font-semibold px-2 py-0.5 rounded-full ${badgeColor}">
                        ${status.replace('_', ' ')}
                    </span>
                </div>
                <div class="flex justify-between items-center text-sm">
                    <span class="text-gray-500">Paid: <strong class="text-green-600">${paidAmt}</strong></span>
                    <span class="text-xs text-gray-400">${p.paymentMode || '-'}</span>
                </div>
            </div>
        `;
    }).join('');
    
    makePagination('payPagination', filteredPayments.length, payPage, payPageSize, 'changePayPage');
}

function changePayPage(page) {
    payPage = page;
    renderPaymentsTable();
}

function getStatusBadgeColor(status) {
    const norm = String(status || 'PENDING').toUpperCase();
    if (norm === 'PAID') return 'bg-green-100 text-green-700';
    if (norm === 'PARTIALLY_PAID') return 'bg-orange-100 text-orange-700';
    if (norm === 'OVERDUE') return 'bg-red-100 text-red-700';
    if (norm === 'CANCELLED') return 'bg-neutral-200 text-neutral-800';
    return 'bg-blue-100 text-blue-700'; // Pending/Sent
}

function formatCurrency(amount) {
    return '₹' + Number(amount || 0).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

function showPaymentDetails(id) {
    const payment = allPayments.find(p => p.id === id);
    if (!payment) return;
    
    document.getElementById('modalCustomerName').textContent = payment.customerName || '-';
    document.getElementById('modalInvoiceNumber').textContent = payment.invoiceNumber || '-';
    document.getElementById('modalInvoiceAmount').textContent = payment.invoiceAmount !== undefined ? formatCurrency(payment.invoiceAmount) : '-';
    document.getElementById('modalInvoiceStatus').textContent = payment.status || '-';
    
    document.getElementById('modalPaidAmount').textContent = formatCurrency(payment.amount);
    document.getElementById('modalRemainingBalance').textContent = payment.remainingBalance !== undefined ? formatCurrency(payment.remainingBalance) : '-';
    document.getElementById('modalPaymentDate').textContent = payment.paymentDate || '-';
    document.getElementById('modalPaymentMode').textContent = payment.paymentMode || '-';
    document.getElementById('modalReferenceNumber').textContent = payment.referenceNo || '-';
    document.getElementById('modalNotes').textContent = payment.notes || '-';
    
    // Format Created Date
    if (payment.createdAt) {
        try {
            const dt = new Date(payment.createdAt);
            document.getElementById('modalCreatedDate').textContent = dt.toLocaleString();
        } catch (e) {
            document.getElementById('modalCreatedDate').textContent = payment.createdAt;
        }
    } else {
        document.getElementById('modalCreatedDate').textContent = '-';
    }
    
    document.getElementById('modalCreatedBy').textContent = payment.createdBy || '-';
    
    document.getElementById('paymentDetailsModal').classList.remove('hidden');
}

function exportPayments(format) {
    if (filteredPayments.length === 0) {
        showToast("No payments to export");
        return;
    }
    
    if (format === 'excel' || format === 'csv') {
        let csvContent = "\ufeff"; // BOM for Excel
        csvContent += "Payment Date,Invoice Number,Customer Name,Payment Mode,Reference Number,Invoice Amount,Paid Amount,Remaining Balance,Status,Notes\r\n";
        
        filteredPayments.forEach(p => {
            const row = [
                p.paymentDate || '',
                p.invoiceNumber || '',
                `"${(p.customerName || '').replace(/"/g, '""')}"`,
                p.paymentMode || '',
                p.referenceNo || '',
                p.invoiceAmount || 0,
                p.amount || 0,
                p.remainingBalance || 0,
                p.status || 'Pending',
                `"${(p.notes || '').replace(/"/g, '""')}"`
            ];
            csvContent += row.join(",") + "\r\n";
        });
        
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement("a");
        const url = URL.createObjectURL(blob);
        link.setAttribute("href", url);
        const filename = format === 'excel' ? 'payment_history.xls' : 'payment_history.csv';
        link.setAttribute("download", filename);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        showToast(`Exported successfully as ${format.toUpperCase()}`);
    } 
    else if (format === 'pdf') {
        // Open PDF print view
        const printWindow = window.open('', '_blank');
        let html = `
            <html>
            <head>
                <title>Payment History Export</title>
                <style>
                    body { font-family: sans-serif; padding: 20px; }
                    table { width: 100%; border-collapse: collapse; margin-top: 20px; }
                    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; font-size: 12px; }
                    th { background-color: #f2f2f2; }
                    h2 { color: #333; }
                </style>
            </head>
            <body>
                <h2>Payment History Report</h2>
                <p>Generated on: ${new Date().toLocaleString()}</p>
                <table>
                    <thead>
                        <tr>
                            <th>Payment Date</th>
                            <th>Invoice Number</th>
                            <th>Customer Name</th>
                            <th>Mode</th>
                            <th>Reference No</th>
                            <th>Invoice Amount</th>
                            <th>Paid Amount</th>
                            <th>Remaining Balance</th>
                            <th>Status</th>
                        </tr>
                    </thead>
                    <tbody>
        `;
        
        filteredPayments.forEach(p => {
            html += `
                <tr>
                    <td>${p.paymentDate || '-'}</td>
                    <td>${p.invoiceNumber || '-'}</td>
                    <td>${p.customerName || '-'}</td>
                    <td>${p.paymentMode || '-'}</td>
                    <td>${p.referenceNo || '-'}</td>
                    <td>₹${(p.invoiceAmount || 0).toFixed(2)}</td>
                    <td>₹${(p.amount || 0).toFixed(2)}</td>
                    <td>₹${(p.remainingBalance || 0).toFixed(2)}</td>
                    <td>${p.status || 'Pending'}</td>
                </tr>
            `;
        });
        
        html += `
                    </tbody>
                </table>
                <script>window.print();</script>
            </body>
            </html>
        `;
        
        printWindow.document.write(html);
        printWindow.document.close();
    }
}