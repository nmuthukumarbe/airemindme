// Sales Dashboard Client-Side Controller
// Handles fetching dynamic sales dashboard data and populating all elements

function formatCurrency(amount) {
    return '₹' + Number(amount || 0).toLocaleString('en-IN', { maximumFractionDigits: 0 });
}

function formatShortValue(num) {
    const val = Number(num || 0);
    if (val >= 100000) {
        return '₹' + (val / 100000).toFixed(2) + 'L';
    } else if (val >= 1000) {
        return '₹' + (val / 1000).toFixed(1) + 'K';
    }
    return '₹' + val.toLocaleString('en-IN', { maximumFractionDigits: 0 });
}

async function loadDashboardData() {
    console.log("Loading dashboard data...");
    try {
        const response = await fetch('/api/sales/dashboard');
        if (!response.ok) {
            throw new Error("Failed to fetch dashboard data");
        }
        const data = await response.json();
        console.log("Dashboard data received:", data);

        populateKPIs(data);
        populateRevenueChart(data.monthlyRevenue);
        populateTopCustomers(data.topCustomers);
        populateStatusBreakdown(data.statusBreakdown);
        populateRecentInvoices(data.recentInvoices);

    } catch (error) {
        console.error("Error loading dashboard data:", error);
        // Show fallback values if error occurs or show toast
        if (typeof showToast === 'function') {
            showToast("Failed to load sales dashboard metrics", "error");
        }
    }
}

function populateKPIs(data) {
    document.getElementById('kpi-total-revenue').innerHTML = formatShortValue(data.totalRevenue);
    document.getElementById('kpi-paid').innerHTML = formatShortValue(data.totalCollected);
    document.getElementById('kpi-paid-invoices').textContent = `${data.paidInvoices} invoice${data.paidInvoices !== 1 ? 's' : ''}`;
    
    document.getElementById('kpi-outstanding').innerHTML = formatShortValue(data.outstandingAmount);
    document.getElementById('kpi-outstanding-invoices').textContent = `${data.unpaidInvoices} invoice${data.unpaidInvoices !== 1 ? 's' : ''}`;
    
    document.getElementById('kpi-overdue').innerHTML = formatShortValue(data.overdueAmount);
    document.getElementById('kpi-overdue-invoices').textContent = `${data.overdueInvoices} invoice${data.overdueInvoices !== 1 ? 's' : ''}`;
    
    document.getElementById('kpi-avg-invoice-value').innerHTML = formatCurrency(data.averageInvoiceValue);
    document.getElementById('kpi-customers-billed').textContent = data.totalCustomersBilled;
    document.getElementById('kpi-total-invoices').textContent = data.totalInvoices;
    
    const collectionRate = Number(data.collectionRate || 0);
    document.getElementById('kpi-collection-rate').textContent = `${collectionRate.toFixed(1)}%`;
    
    // Set labels showing status
    document.getElementById('kpi-total-revenue-label').innerHTML = 'Total generated revenue';
    document.getElementById('kpi-avg-invoice-value-label').innerHTML = 'Avg value per invoice';
    document.getElementById('kpi-collection-rate-label').innerHTML = 'Revenue collection efficiency';
}

function populateRevenueChart(monthlyRevenue) {
    const container = document.getElementById('revenue-chart-container');
    if (!container) return;

    if (!monthlyRevenue || monthlyRevenue.length === 0) {
        container.innerHTML = `<p class="text-sm text-gray-400 py-10 w-full text-center">No revenue data available</p>`;
        return;
    }

    // Find max value to scale chart
    const maxVal = Math.max(...monthlyRevenue.map(m => Number(m.revenue || 0)), 1);

    const html = monthlyRevenue.map((m, index) => {
        const rev = Number(m.revenue || 0);
        const pct = (rev / maxVal) * 100;
        
        // Colors mapping from light indigo to deep indigo
        let bg = '#c7d2fe'; // default light
        if (index === monthlyRevenue.length - 1) bg = '#4f46e5'; // current month (deep)
        else if (index === monthlyRevenue.length - 2) bg = '#6366f1';
        else if (index === monthlyRevenue.length - 3) bg = '#818cf8';
        else if (index === monthlyRevenue.length - 4) bg = '#a5b4fc';

        const displayLabel = formatShortValue(rev);

        return `
            <div class="flex-1 flex flex-col items-center gap-1 h-full justify-end">
              <span class="text-[9px] text-gray-400 select-none">${displayLabel}</span>
              <div style="height: ${Math.max(pct, 5)}%; background: ${bg}; width: 100%;" class="rounded-t-md transition-all duration-500 hover:opacity-80 cursor-pointer" title="${m.month}: ${formatCurrency(rev)}"></div>
              <span class="text-[10px] text-gray-400 select-none">${m.month}</span>
            </div>
        `;
    }).join('');

    container.innerHTML = html;

    // Populate period range
    if (monthlyRevenue.length >= 2) {
        document.getElementById('revenue-chart-period').textContent = 
            `${monthlyRevenue[0].month} – ${monthlyRevenue[monthlyRevenue.length - 1].month}`;
    }
}

function populateTopCustomers(topCustomers) {
    const container = document.getElementById('top-customers-list');
    if (!container) return;

    if (!topCustomers || topCustomers.length === 0) {
        container.innerHTML = `<p class="text-sm text-gray-400 py-6 text-center">No customer data available</p>`;
        return;
    }

    const maxRevenue = Math.max(...topCustomers.map(c => Number(c.totalRevenue || 0)), 1);

    const CUSTOMER_COLORS = [
        { circle: 'bg-indigo-100 text-indigo-700', bar: 'bg-indigo-600' },
        { circle: 'bg-purple-100 text-purple-700', bar: 'bg-purple-500' },
        { circle: 'bg-blue-100 text-blue-700', bar: 'bg-blue-500' },
        { circle: 'bg-pink-100 text-pink-700', bar: 'bg-pink-500' },
        { circle: 'bg-emerald-100 text-emerald-700', bar: 'bg-emerald-500' }
    ];

    const html = topCustomers.map((cust, idx) => {
        const rev = Number(cust.totalRevenue || 0);
        const pct = (rev / maxRevenue) * 100;
        const color = CUSTOMER_COLORS[idx % CUSTOMER_COLORS.length];
        const firstLetter = (cust.customerName || 'U').charAt(0).toUpperCase();

        return `
            <div class="flex items-center gap-3">
                <div class="w-7 h-7 rounded-full ${color.circle} flex items-center justify-center text-xs font-bold shrink-0">
                  ${firstLetter}
                </div>
                <div class="flex-1">
                  <div class="flex justify-between">
                    <span class="text-sm font-medium text-gray-800 truncate max-w-[150px] sm:max-w-[200px]" title="${cust.customerName}">${cust.customerName}</span>
                    <span class="text-sm font-bold text-gray-900">${formatCurrency(rev)}</span>
                  </div>
                  <div class="w-full bg-gray-100 rounded-full h-1.5 mt-1">
                    <div class="${color.bar} h-1.5 rounded-full transition-all duration-500" style="width: ${pct}%"></div>
                  </div>
                </div>
            </div>
        `;
    }).join('');

    container.innerHTML = html;
}

function populateStatusBreakdown(statusBreakdown) {
    const donutContainer = document.getElementById('status-donut-chart');
    const legendContainer = document.getElementById('status-legend');
    if (!donutContainer || !legendContainer) return;

    let total = 0;
    for (const key in statusBreakdown) {
        total += statusBreakdown[key];
    }

    // Render Donut Chart
    const r = 46;
    const circumference = 2 * Math.PI * r; // ~289.026
    let currentOffset = 0;
    let donutHtml = `<svg viewBox="0 0 120 120" class="w-32 h-32">`;
    donutHtml += `<circle cx="60" cy="60" r="${r}" fill="none" stroke="#e5e7eb" stroke-width="16" />`;

    const statuses = [
        { name: 'Paid', color: '#4f46e5', legendColor: 'bg-indigo-600', key: 'Paid' },
        { name: 'Partially Paid', color: '#f97316', legendColor: 'bg-orange-500', key: 'Partially Paid' },
        { name: 'Sent', color: '#3b82f6', legendColor: 'bg-blue-500', key: 'Sent' },
        { name: 'Overdue', color: '#ef4444', legendColor: 'bg-red-500', key: 'Overdue' },
        { name: 'Draft', color: '#9ca3af', legendColor: 'bg-gray-400', key: 'Draft' }
    ];

    if (total > 0) {
        statuses.forEach(s => {
            const count = statusBreakdown[s.key] || 0;
            if (count > 0) {
                const pct = count / total;
                const dashArrayVal = pct * circumference;
                const dashOffsetVal = currentOffset;
                
                donutHtml += `<circle cx="60" cy="60" r="${r}" fill="none" stroke="${s.color}" stroke-width="16" 
                          stroke-dasharray="${dashArrayVal.toFixed(1)} ${(circumference - dashArrayVal).toFixed(1)}" 
                          stroke-dashoffset="-${dashOffsetVal.toFixed(1)}" 
                          transform="rotate(-90 60 60)" />`;
                          
                currentOffset += dashArrayVal;
            }
        });
    }
    donutHtml += `<text x="60" y="57" text-anchor="middle" font-size="11" font-weight="800" fill="#1f2937">${total}</text>`;
    donutHtml += `<text x="60" y="70" text-anchor="middle" font-size="7" fill="#6b7280">invoices</text>`;
    donutHtml += `</svg>`;
    donutContainer.innerHTML = donutHtml;

    // Render Legend
    const legendHtml = statuses.map(item => {
        const count = statusBreakdown[item.key] || 0;
        const pct = total > 0 ? Math.round((count / total) * 100) : 0;
        return `
          <div class="flex items-center justify-between hover:bg-gray-50 p-1.5 rounded-lg transition-colors">
            <div class="flex items-center gap-2">
              <div class="w-3 h-3 rounded-full ${item.legendColor}"></div>
              <span class="text-sm text-gray-600">${item.name}</span>
            </div>
            <span class="text-sm font-bold text-gray-900">${count} (${pct}%)</span>
          </div>
        `;
    }).join('');
    legendContainer.innerHTML = legendHtml;
}

function populateRecentInvoices(recentInvoices) {
    const container = document.getElementById('recent-invoices-list');
    if (!container) return;

    if (!recentInvoices || recentInvoices.length === 0) {
        container.innerHTML = `<p class="text-sm text-gray-400 py-10 text-center">No recent invoices found</p>`;
        return;
    }

    const STATUS_COLORS = {
        'draft': 'bg-gray-100 text-gray-600',
        'sent': 'bg-blue-100 text-blue-700',
        'partially_paid': 'bg-orange-100 text-orange-700',
        'paid': 'bg-green-100 text-green-700',
        'overdue': 'bg-red-100 text-red-700',
        'cancelled': 'bg-gray-100 text-gray-600'
    };

    const html = recentInvoices.map(inv => {
        const firstChar = (inv.customerName || 'U').charAt(0).toUpperCase();
        const statusKey = (inv.status || 'draft').toLowerCase();
        const statusClass = STATUS_COLORS[statusKey] || 'bg-gray-100 text-gray-600';
        const displayStatus = inv.status ? inv.status.replace('_', ' ') : 'Draft';

        return `
            <div onclick="window.location.href='invoice-detail.html?id=${inv.id}'"
              class="flex items-center justify-between p-3 rounded-xl hover:bg-gray-50 cursor-pointer transition-colors border border-transparent hover:border-gray-100">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-full bg-indigo-50 flex items-center justify-center text-xs font-bold text-indigo-600 shrink-0">
                  ${firstChar}
                </div>
                <div class="min-w-0">
                  <p class="text-sm font-semibold text-gray-800 truncate max-w-[120px] sm:max-w-[180px]">${inv.customerName || 'Walk-in Customer'}</p>
                  <p class="text-xs text-gray-400 truncate">${inv.invoiceNumber || 'ID: ' + inv.id} · ${inv.invoiceDate || '-'}</p>
                </div>
              </div>
              <div class="text-right shrink-0">
                <p class="text-sm font-bold text-gray-900">${formatCurrency(inv.grandTotal)}</p>
                <span class="text-[10px] font-semibold px-2 py-0.5 rounded-full ${statusClass}">
                  ${displayStatus}
                </span>
              </div>
            </div>
        `;
    }).join('');

    container.innerHTML = html;
}

// Load on page load
document.addEventListener('DOMContentLoaded', () => {
    loadDashboardData();
});
