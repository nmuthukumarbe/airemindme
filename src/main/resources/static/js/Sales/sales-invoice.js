let allInvoices = [];

async function loadInvoices() {
    try {

        const res = await fetch('/api/invoices');

        if (!res.ok) {
            throw new Error("Failed");
        }

        const data = await res.json();

        allInvoices = data.content || [];

        currentInvList = allInvoices;

        renderInvoicePage();

        console.log("Invoices Loaded:", allInvoices);


    } catch (e) {
        console.error(e);
        showToast("Failed to load invoices", "error");
    }
}
function filterInvoices() {

    INV_PAGE = 1;

    const q = (document.getElementById('invSearch').value || '').toLowerCase();

    let list = allInvoices.filter(inv => {

        if (activeStatus !== 'all' && inv.status.toLowerCase() !== activeStatus)
            return false;

        if (
            q &&
            !String(inv.invoiceNumber || '').toLowerCase().includes(q) &&
            !String(inv.customerId || '').includes(q)
        ) {
            return false;
        }

        return true;
    });

    currentInvList = list;

    renderInvoicePage();
}

function renderInvoiceTable(list, totalCount) {

    const tbody = document.getElementById('invTableBody');
    const cards = document.getElementById('invCards');
    const noMsg = document.getElementById('noInvMsg');

    const total = totalCount !== undefined
        ? totalCount
        : list.length;

    document.getElementById('invCount').textContent =
        'Showing ' + list.length + ' of ' + total +
        ' invoice' + (total !== 1 ? 's' : '');

    if (!list.length) {
        tbody.innerHTML = '';
        cards.innerHTML = '';
        noMsg.classList.remove('hidden');
        return;
    }

    noMsg.classList.add('hidden');

    const actionBtn = (inv) => {

        const status = (inv.status || '').toLowerCase();

        if (status === 'paid') {
            return `
                <button
                    onclick="window.location.href='invoice-detail.html?id=${inv.id}'"
                    class="text-xs px-2.5 py-1 rounded-lg border border-gray-200 text-gray-600 hover:bg-gray-50">
                    View
                </button>
            `;
        }

        if (status === 'draft') {
            return `
                <button
                    onclick="window.location.href='invoice-create.html?id=${inv.id}'"
                    class="text-xs px-2.5 py-1 rounded-lg border border-indigo-200 text-indigo-600 hover:bg-indigo-50">
                    Edit
                </button>
            `;
        }

        return `
            <button
                onclick="window.location.href='invoice-detail.html?id=${inv.id}'"
                class="text-xs px-2.5 py-1 rounded-lg bg-indigo-600 text-white hover:bg-indigo-700">
                Pay
            </button>
        `;
    };

    tbody.innerHTML = list.map(inv => `

        <tr class="hover:bg-gray-50 cursor-pointer"
            onclick="window.location.href='invoice-detail.html?id=${inv.id}'">

            <td class="px-5 py-3.5 text-sm font-semibold text-indigo-600">
                ${inv.invoiceNumber || inv.id}
            </td>

            <td class="px-4 py-3.5 text-sm text-gray-800">
                ${inv.customerName || '-'}
            </td>

            <td class="px-4 py-3.5 text-sm text-gray-500">
                ${inv.invoiceDate || '-'}
            </td>

            <td class="px-4 py-3.5 text-sm text-gray-500">
                ${inv.dueDate || '-'}
            </td>

            <td class="px-4 py-3.5 text-sm font-bold text-gray-900 text-right">
                ₹${Number(inv.grandTotal || 0).toLocaleString('en-IN')}
            </td>

            <td class="px-4 py-3.5 text-center">
                <span class="text-xs font-semibold px-2.5 py-1 rounded-full ${STATUS_COLORS[(inv.status || '').toLowerCase()] || 'bg-gray-100 text-gray-600'}">
                    ${inv.status || 'Draft'}
                </span>
            </td>

            <td class="px-4 py-3.5 text-center"
                onclick="event.stopPropagation()">

                ${actionBtn(inv)}

            </td>

        </tr>

    `).join('');

    cards.innerHTML = list.map(inv => `

        <div
            class="bg-white rounded-2xl border border-gray-100 shadow-sm p-4 cursor-pointer"
            onclick="window.location.href='invoice-detail.html?id=${inv.id}'">

            <div class="flex items-start justify-between mb-2">

                <div>
                    <p class="text-sm font-semibold text-gray-900">
                        Customer #${inv.customerId || '-'}
                    </p>

                    <p class="text-xs text-gray-400">
                        ${inv.invoiceNumber || inv.id}
                    </p>
                </div>

                <span class="text-xs font-semibold px-2.5 py-1 rounded-full ${STATUS_COLORS[(inv.status || '').toLowerCase()] || 'bg-gray-100 text-gray-600'}">
                    ${inv.status || 'Draft'}
                </span>

            </div>

            <div class="flex items-center justify-between">

                <div>
                    <p class="text-xs text-gray-400">
                        Due: ${inv.dueDate || '-'}
                    </p>
                </div>

                <p class="text-base font-bold text-gray-900">
                    ₹${Number(inv.grandTotal || 0).toLocaleString('en-IN')}
                </p>

            </div>

        </div>

    `).join('');
}




function applyPreset(preset) {
    const now = new Date();
    let from, to;
    const fmt = d => d.toISOString().split('T')[0];
    const startOf = (d, unit) => { const r = new Date(d); if (unit === 'week') { r.setDate(d.getDate() - d.getDay()); } else if (unit === 'month') { r.setDate(1); } else if (unit === 'quarter') { r.setMonth(Math.floor(d.getMonth() / 3) * 3, 1); } else if (unit === 'year') { r.setMonth(0, 1); } r.setHours(0, 0, 0, 0); return r; };
    if (preset === 'today') { from = to = fmt(now); }
    else if (preset === 'yesterday') { const y = new Date(now); y.setDate(now.getDate() - 1); from = to = fmt(y); }
    else if (preset === 'week') { from = fmt(startOf(now, 'week')); to = fmt(now); }
    else if (preset === 'month') { from = fmt(startOf(now, 'month')); to = fmt(now); }
    else if (preset === 'lastmonth') { const lm = new Date(now); lm.setDate(0); const lms = new Date(lm); lms.setDate(1); from = fmt(lms); to = fmt(lm); }
    else if (preset === 'quarter') { from = fmt(startOf(now, 'quarter')); to = fmt(now); }
    else if (preset === 'year') { from = fmt(startOf(now, 'year')); to = fmt(now); }

    document.getElementById('dateFrom').value = from;
    document.getElementById('dateTo').value = to;
    document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    event.target.classList.add('active');
    activeDateFrom = from; activeDateTo = to;
    updateDateLabel();
    filterInvoices();
}

function applyCustomDate() {
    activeDateFrom = document.getElementById('dateFrom').value || null;
    activeDateTo = document.getElementById('dateTo').value || null;
    document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
    updateDateLabel();
    filterInvoices();
}

function clearDateFilter() {
    activeDateFrom = null; activeDateTo = null;
    document.getElementById('dateFrom').value = '';
    document.getElementById('dateTo').value = '';
    document.querySelectorAll('.preset-btn').forEach(b => b.classList.remove('active'));
    updateDateLabel();
    filterInvoices();
    document.getElementById('dateRangeDropdown').classList.remove('open');
}

function updateDateLabel() {
    const lbl = document.getElementById('dateRangeLabel');
    if (activeDateFrom && activeDateTo) {
        const fmtD = s => { const [y, m, d] = s.split('-'); return d + '/' + m + '/' + y.slice(2); };
        lbl.textContent = fmtD(activeDateFrom) + ' – ' + fmtD(activeDateTo);
    } else { lbl.textContent = 'Date Range'; }
}

// ---- FILTER + RENDER ----
function setStatusFilter(status, btn) {
    activeStatus = status;
    document.querySelectorAll('.filter-pill').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    filterInvoices();
}
