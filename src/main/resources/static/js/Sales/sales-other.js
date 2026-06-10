 // ---- PAYMENTS ----
    const PAYMENTS = [
      { inv: 'INV-2024-001', customer: 'Ramesh Traders', date: '2024-06-14', amount: 18500, mode: 'UPI', ref: 'UPI-7834902' },
      { inv: 'INV-2024-002', customer: 'Priya Enterprises', date: '2024-06-17', amount: 42000, mode: 'Bank Transfer', ref: 'NEFT-112345' },
      { inv: 'INV-2024-005', customer: 'Mehta Pharma', date: '2024-06-22', amount: 67800, mode: 'Cheque', ref: 'CHQ-004521' },
      { inv: 'INV-2024-009', customer: 'Sharma Textiles', date: '2024-06-29', amount: 11250, mode: 'Cash', ref: 'CSH-001' },
    ];
    function renderPayments() {
      document.getElementById('paymentsList').innerHTML = PAYMENTS.map(p => `
    <div class="flex items-center justify-between px-5 py-4 hover:bg-gray-50">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center"><svg class="w-4 h-4 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/></svg></div>
        <div><p class="text-sm font-semibold text-gray-800">${p.customer}</p><p class="text-xs text-gray-400">${p.inv} · ${p.date} · ${p.mode}</p><p class="text-xs text-gray-400">Ref: ${p.ref}</p></div>
      </div>
      <p class="text-sm font-bold text-green-600">+&#8377;${p.amount.toLocaleString('en-IN')}</p>
    </div>
  `).join('');
    }

    // ---- ESTIMATES ----
    const ESTIMATES = [
      { id: 'EST-2024-001', customer: 'Nisha Boutique', date: '2024-06-10', valid: '2024-06-25', amount: 28000, status: 'Sent' },
      { id: 'EST-2024-002', customer: 'Ravi Constructions', date: '2024-06-12', valid: '2024-06-27', amount: 95000, status: 'Accepted' },
      { id: 'EST-2024-003', customer: 'Om Traders', date: '2024-06-15', valid: '2024-06-30', amount: 12500, status: 'Draft' },
      { id: 'EST-2024-004', customer: 'Sunrise Cafe', date: '2024-06-16', valid: '2024-07-01', amount: 8900, status: 'Expired' },
    ];
    const EST_COLORS = { Sent: 'bg-blue-100 text-blue-700', Accepted: 'bg-green-100 text-green-700', Draft: 'bg-gray-100 text-gray-600', Expired: 'bg-red-100 text-red-600' };
    function renderEstimates() {
      document.getElementById('estimatesList').innerHTML = ESTIMATES.map(e => `
    <div class="flex items-center justify-between px-5 py-4 hover:bg-gray-50 cursor-pointer">
      <div><p class="text-sm font-semibold text-gray-800">${e.customer}</p><p class="text-xs text-gray-400">${e.id} · ${e.date} · Valid till ${e.valid}</p></div>
      <div class="text-right"><p class="text-sm font-bold text-gray-900">&#8377;${e.amount.toLocaleString('en-IN')}</p><span class="text-xs font-semibold px-2 py-0.5 rounded-full ${EST_COLORS[e.status]}">${e.status}</span></div>
    </div>
  `).join('');
    }

    // ---- CREDIT NOTES ----
    const CREDIT_NOTES = [
      { id: 'CN-2024-001', customer: 'Suresh & Sons', date: '2024-06-08', amount: 2500, reason: 'Goods returned – damaged', status: 'Applied' },
      { id: 'CN-2024-002', customer: 'Patel Hardware', date: '2024-06-11', amount: 1800, reason: 'Price correction', status: 'Open' },
      { id: 'CN-2024-003', customer: 'Kavya Fashions', date: '2024-06-14', amount: 4200, reason: 'Product discount applied post-invoice', status: 'Open' },
    ];
    const CN_COLORS = { Applied: 'bg-green-100 text-green-700', Open: 'bg-orange-100 text-orange-700' };
    function renderCreditNotes() {
      document.getElementById('creditNotesList').innerHTML = CREDIT_NOTES.map(c => `
    <div class="flex items-center justify-between px-5 py-4 hover:bg-gray-50 cursor-pointer">
      <div><p class="text-sm font-semibold text-gray-800">${c.customer}</p><p class="text-xs text-gray-400">${c.id} · ${c.date}</p><p class="text-xs text-gray-400">${c.reason}</p></div>
      <div class="text-right"><p class="text-sm font-bold text-red-600">-&#8377;${c.amount.toLocaleString('en-IN')}</p><span class="text-xs font-semibold px-2 py-0.5 rounded-full ${CN_COLORS[c.status]}">${c.status}</span></div>
    </div>
  `).join('');
    }

    function initQRProducts() {
      QR_ALL_PRODUCTS = RMData.getProducts();
      QR_ALL_PRODUCTS.forEach(p => { QR_PRODUCTS_MAP[p.sku] = p; QR_PRODUCTS_MAP[p.id] = p; });
      buildCategoryPills();
      filterQRProducts();
      qrUpdateKPIs();
    }

    function qrUpdateKPIs() {
      const bills = JSON.parse(localStorage.getItem('rm_qr_bills') || '[]');
      const today = new Date().toISOString().split('T')[0];
      const todayBills = bills.filter(b => b.date === today);
      const revenue = todayBills.reduce((s, b) => s + (b.amount || 0), 0);
      const lowStock = QR_ALL_PRODUCTS.filter(p => (p.stock || 0) <= (p.reorderLevel || 5)).length;
      const el = id => document.getElementById(id);
      if (el('qrKpiToday')) el('qrKpiToday').textContent = todayBills.length;
      if (el('qrKpiRevenue')) el('qrKpiRevenue').textContent = '₹' + revenue.toLocaleString('en-IN');
      if (el('qrKpiProducts')) el('qrKpiProducts').textContent = QR_ALL_PRODUCTS.length;
      if (el('qrKpiLowStock')) el('qrKpiLowStock').textContent = lowStock;
    }

    function buildCategoryPills() {
      const cats = ['all', ...new Set(QR_ALL_PRODUCTS.map(p => p.category).filter(Boolean))];
      const container = document.getElementById('qrCategoryPills');
      if (!container) return;
      container.innerHTML = cats.map(cat =>
        `<button onclick="setQRCategory('${cat}',this)" class="qr-cat-pill shrink-0 px-3 py-1.5 rounded-full text-xs font-semibold transition ${cat === qrActiveCategory ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200'}">${cat === 'all' ? 'All' : cat}</button>`
      ).join('');
    }

    function setQRCategory(cat, btn) {
      qrActiveCategory = cat;
      document.querySelectorAll('.qr-cat-pill').forEach(b => {
        b.className = 'qr-cat-pill shrink-0 px-3 py-1.5 rounded-full text-xs font-semibold transition ' + (b === btn ? 'bg-indigo-600 text-white' : 'bg-gray-100 text-gray-600 hover:bg-gray-200');
      });
      filterQRProducts();
    }

    function filterQRProducts() {
      const q = (document.getElementById('qrProductSearch')?.value || '').toLowerCase();
      let list = QR_ALL_PRODUCTS;
      if (qrActiveCategory !== 'all') list = list.filter(p => p.category === qrActiveCategory);
      if (q) list = list.filter(p => p.name.toLowerCase().includes(q) || (p.sku || '').toLowerCase().includes(q));
      renderQRProductGrid(list);
    }

    function renderQRProductGrid(list) {
      const grid = document.getElementById('qrProductGrid');
      const noMsg = document.getElementById('qrNoProducts');
      if (!grid) return;
      if (!list.length) {
        grid.innerHTML = '';
        if (noMsg) noMsg.classList.remove('hidden');
        return;
      }
      if (noMsg) noMsg.classList.add('hidden');
      const PAY_COLORS = { Electronics: 'bg-blue-50 text-blue-600', Furniture: 'bg-amber-50 text-amber-700', Textiles: 'bg-purple-50 text-purple-700', Pharmacy: 'bg-green-50 text-green-700', Jewellery: 'bg-yellow-50 text-yellow-700' };
      grid.innerHTML = list.slice(0, 30).map(p => {
        const inCart = qrBillItems.find(i => i.sku === p.sku);
        const outOfStock = (p.stock || 0) <= 0;
        const catColor = PAY_COLORS[p.category] || 'bg-gray-50 text-gray-600';
        return `<div onclick="${outOfStock ? '' : `qrAddBySku('${p.sku}')`}" class="rounded-xl border ${outOfStock ? 'border-gray-100 opacity-50 cursor-not-allowed' : inCart ? 'border-indigo-300 bg-indigo-50 cursor-pointer' : 'border-gray-100 hover:border-indigo-200 hover:bg-indigo-50/40 cursor-pointer'} p-3 transition select-none">
      <p class="text-xs font-bold text-gray-800 truncate leading-tight">${p.name}</p>
      <p class="text-[10px] text-gray-400 mb-1.5">${p.sku}</p>
      <div class="flex items-center justify-between">
        <p class="text-sm font-extrabold text-indigo-600">₹${(p.price || 0).toLocaleString('en-IN')}</p>
        <span class="text-[10px] font-semibold px-1.5 py-0.5 rounded-full ${outOfStock ? 'bg-red-100 text-red-600' : 'bg-green-100 text-green-600'}">${outOfStock ? 'OOS' : 'Stk:' + (p.stock || 0)}</span>
      </div>
      ${inCart ? `<p class="text-[10px] font-bold text-indigo-600 mt-1">✓ In cart (${inCart.qty})</p>` : ''}
    </div>`;
      }).join('');
    }

    function simulateQRScan() {
      const el = document.getElementById('scanLine');
      el.classList.remove('hidden');
      el.style.animation = 'none'; el.offsetHeight; el.style.animation = '';
      const avail = QR_ALL_PRODUCTS.filter(p => (p.stock || 0) > 0);
      const p = avail[Math.floor(Math.random() * avail.length)];
      if (!p) return;
      setTimeout(() => { el.classList.add('hidden'); qrAddBySku(p.sku); showToast('QR scanned: ' + p.name); }, 1200);
    }

    function qrAddBySku(sku) {
      const p = QR_PRODUCTS_MAP[sku];
      if (!p) { showToast('Product not found: ' + sku); return; }
      if ((p.stock || 0) <= 0) { showToast('⚠️ Out of stock: ' + p.name); return; }
      const existing = qrBillItems.find(i => i.sku === p.sku);
      if (existing) {
        if (existing.qty >= (p.stock || 99)) { showToast('⚠️ Max stock reached for ' + p.name); return; }
        existing.qty++;
      } else {
        qrBillItems.push({ id: p.id, sku: p.sku, name: p.name, price: p.price || 0, cost: p.cost || 0, gst: 18, qty: 1, stock: p.stock || 99 });
      }
      renderQRBill();
      filterQRProducts();
    }

      // ========== ESTIMATES ==========
    let EST_PAGE = 1; const EST_PER = 6;
    function renderEstimates(p) {
      if (p) EST_PAGE = p;
      const items = ESTIMATES.slice((EST_PAGE - 1) * EST_PER, EST_PAGE * EST_PER);
      document.getElementById('estimatesList').innerHTML = items.map(e => `
    <div class="flex items-center justify-between px-5 py-4 hover:bg-gray-50 cursor-pointer">
      <div><p class="text-sm font-semibold text-gray-800">${e.customer}</p><p class="text-xs text-gray-400">${e.id} · ${e.date} · Valid till ${e.valid}</p></div>
      <div class="text-right"><p class="text-sm font-bold text-gray-900">&#8377;${e.amount.toLocaleString('en-IN')}</p><span class="text-xs font-semibold px-2 py-0.5 rounded-full ${EST_COLORS[e.status]}">${e.status}</span></div>
    </div>`).join('');
      makePagination('estPagination', ESTIMATES.length, EST_PAGE, EST_PER, 'renderEstimates');
    }

    // ========== CREDIT NOTES ==========
    let CN_PAGE = 1; const CN_PER = 6;
    function renderCreditNotes(p) {
      if (p) CN_PAGE = p;
      const items = CREDIT_NOTES.slice((CN_PAGE - 1) * CN_PER, CN_PAGE * CN_PER);
      document.getElementById('creditNotesList').innerHTML = items.map(c => `
    <div class="flex items-center justify-between px-5 py-4 hover:bg-gray-50 cursor-pointer">
      <div><p class="text-sm font-semibold text-gray-800">${c.customer}</p><p class="text-xs text-gray-400">${c.id} · ${c.date}</p><p class="text-xs text-gray-400">${c.reason}</p></div>
      <div class="text-right"><p class="text-sm font-bold text-red-600">-&#8377;${c.amount.toLocaleString('en-IN')}</p><span class="text-xs font-semibold px-2 py-0.5 rounded-full ${CN_COLORS[c.status]}">${c.status}</span></div>
    </div>`).join('');
      makePagination('cnPagination', CREDIT_NOTES.length, CN_PAGE, CN_PER, 'renderCreditNotes');
    }

    // ========== QR BILLING ==========
    let qrBillItems = [];
    let qrPayMode = 'Cash';
    let qrActiveCategory = 'all';
    const QR_PRODUCTS_MAP = {};
    let QR_ALL_PRODUCTS = [];
function addQRItem() {
      const val = (document.getElementById('qrCodeInput')?.value || '').trim();
      if (!val) return;
      const match = QR_PRODUCTS_MAP[val] || QR_ALL_PRODUCTS.find(p => p.name.toLowerCase().includes(val.toLowerCase()));
      if (match) { qrAddBySku(match.sku || match.id); } else { showToast('No product found for: ' + val); }
      document.getElementById('qrCodeInput').value = '';
    }

    function changeQRQty(sku, delta) {
      const item = qrBillItems.find(i => i.sku === sku);
      if (!item) return;
      item.qty = Math.max(1, Math.min(item.qty + delta, item.stock));
      renderQRBill();
      filterQRProducts();
    }

    function removeQRItem(sku) {
      qrBillItems = qrBillItems.filter(i => i.sku !== sku);
      renderQRBill();
      filterQRProducts();
    }

    function clearQRBill() {
      qrBillItems = [];
      if (document.getElementById('qrDiscount')) document.getElementById('qrDiscount').value = 0;
      renderQRBill();
      filterQRProducts();
    }

    function renderQRBill() {
      const container = document.getElementById('qrBillItems');
      const empty = document.getElementById('qrBillEmpty');
      const countEl = document.getElementById('qrBillItemCount');
      const discountPct = parseFloat(document.getElementById('qrDiscount')?.value || 0) || 0;
      if (!qrBillItems.length) {
        container.innerHTML = '';
        if (empty) { empty.style.display = 'flex'; container.appendChild(empty); }
        if (countEl) countEl.textContent = '0 items';
        ['qrSubtotal', 'qrDiscountAmt', 'qrGst', 'qrTotal'].forEach(id => { const e = document.getElementById(id); if (e) e.textContent = id === 'qrDiscountAmt' ? '−₹0' : '₹0'; });
        return;
      }
      if (empty) empty.style.display = 'none';
      let subtotal = 0;
      container.innerHTML = qrBillItems.map(item => {
        const line = item.qty * item.price;
        subtotal += line;
        return `<div class="flex items-center gap-2 py-2 border-b border-gray-50">
      <div class="flex-1 min-w-0">
        <p class="text-xs font-semibold text-gray-800 truncate">${item.name}</p>
        <p class="text-[10px] text-gray-400">₹${item.price.toLocaleString('en-IN')} each</p>
      </div>
      <div class="flex items-center gap-1 shrink-0">
        <button onclick="changeQRQty('${item.sku}',-1)" class="w-6 h-6 rounded-lg border border-gray-200 flex items-center justify-center text-gray-600 hover:bg-gray-100 text-xs font-bold">−</button>
        <span class="text-xs font-bold w-5 text-center">${item.qty}</span>
        <button onclick="changeQRQty('${item.sku}',1)" class="w-6 h-6 rounded-lg border border-gray-200 flex items-center justify-center text-gray-600 hover:bg-gray-100 text-xs font-bold">+</button>
        <button onclick="removeQRItem('${item.sku}')" class="w-6 h-6 rounded-lg text-red-400 hover:bg-red-50 flex items-center justify-center ml-0.5 text-xs">✕</button>
      </div>
      <span class="text-xs font-bold text-gray-800 w-16 text-right shrink-0">₹${line.toLocaleString('en-IN')}</span>
    </div>`;
      }).join('');
      const discAmt = subtotal * discountPct / 100;
      const afterDisc = subtotal - discAmt;
      const gst = afterDisc * 0.18;
      const total = afterDisc + gst;
      if (countEl) countEl.textContent = qrBillItems.length + ' item' + (qrBillItems.length !== 1 ? 's' : '');
      const fmt = v => '₹' + v.toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
      document.getElementById('qrSubtotal').textContent = fmt(subtotal);
      document.getElementById('qrDiscountAmt').textContent = '−' + fmt(discAmt);
      document.getElementById('qrGst').textContent = fmt(gst);
      document.getElementById('qrTotal').textContent = fmt(total);
    }

    function setQRPayMode(btn, mode) {
      qrPayMode = mode;
      document.querySelectorAll('.qr-pay-btn').forEach(b => { b.className = 'qr-pay-btn py-2 rounded-xl text-xs font-semibold border-2 border-gray-200 text-gray-600'; });
      btn.className = 'qr-pay-btn py-2 rounded-xl text-xs font-semibold border-2 border-indigo-300 bg-indigo-50 text-indigo-700';
    }

    function processQRBill() {
      if (!qrBillItems.length) { showToast('Add items to bill first'); return; }
      // Reduce stock immediately
      qrBillItems.forEach(item => {
        RMData.adjustStock(item.id, -item.qty, 'Sale', 'QR-BILL-' + Date.now(), 'QR quick bill');
      });
      const custEl = document.getElementById('qrBillCustomer');
      const custName = custEl.options[custEl.selectedIndex].text;
      const custId = custEl.value;
      const discountPct = parseFloat(document.getElementById('qrDiscount')?.value || 0) || 0;
      const invNum = RMData.nextInvoiceNum();
      const today = new Date().toISOString().split('T')[0];
      const due = new Date(Date.now() + 30 * 86400000).toISOString().split('T')[0];

      // Save draft to localStorage so invoice-create can read it
      const draft = {
        id: invNum,
        customer: custName || 'Walk-in Customer',
        customerId: custId || '',
        date: today,
        due,
        payMode: qrPayMode,
        discountPct,
        items: qrBillItems.map(i => ({ name: i.name, sku: i.sku, qty: i.qty, rate: i.price, gst: 18, desc: 'SKU: ' + i.sku }))
      };
      localStorage.setItem('rm_qr_draft', JSON.stringify(draft));

      // Navigate to invoice-create with the draft
      window.location.href = 'invoice-create.html?from=qr';
    }

    function renderRecentQRBills() {
      const bills = JSON.parse(localStorage.getItem('rm_qr_bills') || '[]');
      const tbody = document.getElementById('qrBillsTableBody');
      const mobile = document.getElementById('qrBillsMobile');
      const emptyMsg = document.getElementById('qrBillsEmpty');
      const countEl = document.getElementById('qrBillsTotalCount');
      if (countEl) countEl.textContent = bills.length + ' total';
      if (!bills.length) {
        if (tbody) tbody.innerHTML = '';
        if (mobile) mobile.innerHTML = '';
        if (emptyMsg) emptyMsg.classList.remove('hidden');
        return;
      }
      if (emptyMsg) emptyMsg.classList.add('hidden');
      const PAY_COLORS = { Cash: 'bg-green-100 text-green-700', UPI: 'bg-blue-100 text-blue-700', Card: 'bg-purple-100 text-purple-700', Credit: 'bg-orange-100 text-orange-700' };
      if (tbody) {
        tbody.innerHTML = bills.slice(0, 10).map(b => {
          const ts = b.timestamp ? new Date(b.timestamp) : new Date(b.date);
          const timeStr = ts.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit' });
          const pc = PAY_COLORS[b.payMode] || 'bg-gray-100 text-gray-600';
          return `<tr class="hover:bg-gray-50/50 transition">
        <td class="px-4 py-3 text-sm font-bold text-indigo-600">${b.id}</td>
        <td class="px-4 py-3 text-sm text-gray-700">${b.customer}</td>
        <td class="px-4 py-3 text-xs text-gray-400">${b.date} ${timeStr}</td>
        <td class="px-4 py-3 text-sm text-gray-600">${(b.items || []).length} items</td>
        <td class="px-4 py-3"><span class="text-[11px] font-bold px-2 py-0.5 rounded-full ${pc}">${b.payMode || 'Cash'}</span></td>
        <td class="px-4 py-3 text-sm font-bold text-gray-900 text-right">₹${b.amount.toLocaleString('en-IN')}</td>
      </tr>`;
        }).join('');
      }
      if (mobile) {
        mobile.innerHTML = bills.slice(0, 6).map(b => {
          const pc = PAY_COLORS[b.payMode] || 'bg-gray-100 text-gray-600';
          return `<div class="flex items-center justify-between bg-gray-50 rounded-xl px-4 py-3">
        <div><p class="text-sm font-bold text-indigo-600">${b.id}</p><p class="text-xs text-gray-400">${b.customer} · ${b.date}</p></div>
        <div class="text-right"><p class="text-sm font-bold text-gray-900">₹${b.amount.toLocaleString('en-IN')}</p><span class="text-[10px] font-bold px-1.5 py-0.5 rounded-full ${pc}">${b.payMode || 'Cash'}</span></div>
      </div>`;
        }).join('');
      }
    }
