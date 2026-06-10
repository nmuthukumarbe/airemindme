// Reminder Me — Shared Data Layer (localStorage)
// All pages include this script for cross-page data sync

const RMData = (() => {
  const KEY_INVOICES   = 'rm_invoices';
  const KEY_STOCK      = 'rm_stock';
  const KEY_LEDGER     = 'rm_stock_ledger';
  const KEY_PRODUCTS   = 'rm_products';

  // ---- SEED DATA ----
  const SEED_INVOICES = [
    {id:'INV-2024-001',customer:'Suresh & Sons',customerId:'cust-001',date:'2024-06-01',due:'2024-07-01',amount:28380,status:'paid',items:[{name:'Office Chair Pro X200',qty:3,rate:8500,gst:18}]},
    {id:'INV-2024-002',customer:'Patel Hardware',customerId:'cust-002',date:'2024-06-03',due:'2024-07-03',amount:12980,status:'overdue',items:[{name:'A4 Copy Paper 500 Sheets',qty:20,rate:320,gst:12}]},
    {id:'INV-2024-003',customer:'Nisha Boutique',customerId:'cust-003',date:'2024-06-05',due:'2024-07-05',amount:45200,status:'paid',items:[{name:'Banarasi Saree',qty:10,rate:2800,gst:5}]},
    {id:'INV-2024-004',customer:'Kavya Fashions',customerId:'cust-004',date:'2024-06-15',due:'2024-07-15',amount:70640,status:'unpaid',items:[{name:'Office Chair Pro X200',qty:4,rate:8500,gst:18},{name:'Web Design Consulting',qty:2,rate:5000,gst:18}]},
    {id:'INV-2024-005',customer:'Om Traders',customerId:'cust-005',date:'2024-06-18',due:'2024-07-18',amount:9800,status:'paid',items:[]},
    {id:'INV-2024-006',customer:'Sunrise Cafe',customerId:'cust-006',date:'2024-06-20',due:'2024-07-20',amount:18500,status:'draft',items:[]},
    {id:'INV-2024-007',customer:'Ravi Constructions',customerId:'cust-002',date:'2024-06-22',due:'2024-07-22',amount:95000,status:'paid',items:[]},
    {id:'INV-2024-008',customer:'Om Traders',customerId:'cust-005',date:'2024-06-24',due:'2024-07-24',amount:32000,status:'unpaid',items:[]},
    {id:'INV-2024-009',customer:'Patel Hardware',customerId:'cust-002',date:'2024-06-26',due:'2024-07-26',amount:7800,status:'paid',items:[]},
    {id:'INV-2024-010',customer:'Kavya Fashions',customerId:'cust-004',date:'2024-06-28',due:'2024-07-28',amount:52000,status:'overdue',items:[]},
  ];

  const SEED_PRODUCTS = [
    {id:'P001',name:"Samsung TV 55\"",sku:'ELC-TV-55',cat:'Electronics',stock:45,cost:32000,price:42500,status:'ok',minStock:5},
    {id:'P002',name:'USB-C Cable 2m',sku:'ELC-042',cat:'Electronics',stock:8,cost:180,price:350,status:'low',minStock:10},
    {id:'P003',name:'Banarasi Saree',sku:'TXT-BAN-001',cat:'Textiles',stock:92,cost:1800,price:2800,status:'ok',minStock:20},
    {id:'P004',name:'Cotton T-Shirt XL',sku:'TXT-018',cat:'Textiles',stock:12,cost:220,price:450,status:'low',minStock:20},
    {id:'P005',name:'M8 Hex Bolt (200pc)',sku:'HRD-077',cat:'Hardware',stock:3,cost:180,price:320,status:'low',minStock:10},
    {id:'P006',name:'Paracetamol 500mg',sku:'PHR-001',cat:'Pharma',stock:0,cost:12,price:25,status:'out',minStock:50},
    {id:'P007',name:'LED Bulb 9W',sku:'ELC-LED-9',cat:'Electronics',stock:210,cost:45,price:120,status:'ok',minStock:30},
    {id:'P008',name:'Georgette Dupatta',sku:'TXT-GD-22',cat:'Textiles',stock:38,cost:600,price:1100,status:'ok',minStock:10},
    {id:'P009',name:'Office Chair Pro X200',sku:'CHR-001',cat:'Furniture',stock:24,cost:6500,price:8500,status:'ok',minStock:5},
    {id:'P010',name:'HD Monitor 24"',sku:'MON-006',cat:'Electronics',stock:11,cost:10500,price:14500,status:'low',minStock:5},
    {id:'P011',name:'Wireless Keyboard MK700',sku:'KB-003',cat:'Electronics',stock:17,cost:2200,price:2800,status:'ok',minStock:5},
    {id:'P012',name:'Filing Cabinet 3-Drawer',sku:'CAB-007',cat:'Furniture',stock:6,cost:4200,price:5200,status:'ok',minStock:3},
  ];

  const SEED_LEDGER = [
    {id:'L001',productId:'P001',productName:"Samsung TV 55\"",date:'2024-06-04',type:'OPENING_STOCK',qty:5,balance:45,ref:'OPEN-2024',note:'Opening stock entry'},
    {id:'L002',productId:'P001',productName:"Samsung TV 55\"",date:'2024-06-05',type:'Sale',qty:-1,balance:44,ref:'INV-2024-001',note:'Sold to Suresh & Sons'},
    {id:'L003',productId:'P001',productName:"Samsung TV 55\"",date:'2024-06-06',type:'Sale return',qty:1,balance:45,ref:'RTN-001',note:'Return from customer'},
    {id:'L004',productId:'P001',productName:"Samsung TV 55\"",date:'2024-06-07',type:'Sale',qty:-2,balance:43,ref:'INV-2024-003',note:'Sold to Nisha Boutique'},
    {id:'L005',productId:'P001',productName:"Samsung TV 55\"",date:'2024-06-08',type:'Stock adjustment',qty:7,balance:50,ref:'ADJ-001',note:'Physical count adjustment'},
    {id:'L006',productId:'P009',productName:'Office Chair Pro X200',date:'2024-06-10',type:'OPENING_STOCK',qty:20,balance:20,ref:'OPEN-2024',note:'Opening stock entry'},
    {id:'L007',productId:'P009',productName:'Office Chair Pro X200',date:'2024-06-15',type:'Sale',qty:-4,balance:16,ref:'INV-2024-004',note:'Sold to Kavya Fashions'},
    {id:'L008',productId:'P009',productName:'Office Chair Pro X200',date:'2024-06-18',type:'Stock inward',qty:12,balance:28,ref:'INW-001',note:'Received from supplier'},
  ];

  function init() {
    if (!localStorage.getItem(KEY_INVOICES)) localStorage.setItem(KEY_INVOICES, JSON.stringify(SEED_INVOICES));
    if (!localStorage.getItem(KEY_PRODUCTS)) localStorage.setItem(KEY_PRODUCTS, JSON.stringify(SEED_PRODUCTS));
    if (!localStorage.getItem(KEY_LEDGER))   localStorage.setItem(KEY_LEDGER, JSON.stringify(SEED_LEDGER));
  }

  // ---- INVOICES ----
  function getInvoices() {
    init();
    try { return JSON.parse(localStorage.getItem(KEY_INVOICES)) || []; } catch(e) { return SEED_INVOICES; }
  }
  function saveInvoice(inv) {
    const list = getInvoices();
    const idx = list.findIndex(i => i.id === inv.id);
    if (idx >= 0) list[idx] = inv; else list.unshift(inv);
    localStorage.setItem(KEY_INVOICES, JSON.stringify(list));
    // Reduce stock for each line item
    if (inv.status !== 'draft') {
      (inv.items || []).forEach(item => {
        if (item.productId) adjustStock(item.productId, -item.qty, 'Sale', inv.id, 'Sold to ' + inv.customer);
      });
    }
    return inv;
  }
  function getCustomerInvoices(customerId) {
    return getInvoices().filter(i => i.customerId === customerId || i.customer === customerId);
  }
  function deleteInvoice(id) {
    const list = getInvoices().filter(i => i.id !== id);
    localStorage.setItem(KEY_INVOICES, JSON.stringify(list));
  }
  function nextInvoiceNum() {
    const list = getInvoices();
    const max = list.reduce((m, i) => {
      const n = parseInt((i.id || '').split('-').pop()) || 0;
      return Math.max(m, n);
    }, 10);
    return 'INV-2024-' + String(max + 1).padStart(3, '0');
  }

  // ---- PRODUCTS ----
  function getProducts() {
    init();
    try { return JSON.parse(localStorage.getItem(KEY_PRODUCTS)) || []; } catch(e) { return SEED_PRODUCTS; }
  }
  function saveProduct(p) {
    const list = getProducts();
    const idx = list.findIndex(x => x.id === p.id);
    if (idx >= 0) list[idx] = p; else list.push(p);
    localStorage.setItem(KEY_PRODUCTS, JSON.stringify(list));
  }
  function getProductBySku(sku) {
    return getProducts().find(p => p.sku === sku || p.id === sku);
  }

  // ---- STOCK LEDGER ----
  function getLedger(productId) {
    init();
    const all = JSON.parse(localStorage.getItem(KEY_LEDGER) || '[]');
    return productId ? all.filter(e => e.productId === productId) : all;
  }
  function adjustStock(productId, qty, type, ref, note) {
    const products = getProducts();
    const pIdx = products.findIndex(p => p.id === productId);
    if (pIdx < 0) return;

    const p = products[pIdx];
    const newStock = Math.max(0, (p.stock || 0) + qty);
    p.stock = newStock;
    p.status = newStock === 0 ? 'out' : newStock <= (p.minStock || 5) ? 'low' : 'ok';
    products[pIdx] = p;
    localStorage.setItem(KEY_PRODUCTS, JSON.stringify(products));

    // Add ledger entry
    const ledger = JSON.parse(localStorage.getItem(KEY_LEDGER) || '[]');
    const entry = {
      id: 'L' + Date.now(),
      productId,
      productName: p.name,
      date: new Date().toISOString().split('T')[0],
      type,
      qty,
      balance: newStock,
      ref: ref || '',
      note: note || ''
    };
    ledger.push(entry);
    localStorage.setItem(KEY_LEDGER, JSON.stringify(ledger));
    return entry;
  }
  function recordInward(productId, qty, ref, supplier, cost, note) {
    return adjustStock(productId, qty, 'Stock inward', ref, note || 'Received from ' + supplier);
  }
  function recordOutward(productId, qty, ref, customer, note) {
    return adjustStock(productId, -qty, 'Stock outward', ref, note || 'Outward to ' + customer);
  }

  return { init, getInvoices, saveInvoice, getCustomerInvoices, deleteInvoice, nextInvoiceNum, getProducts, saveProduct, getProductBySku, getLedger, adjustStock, recordInward, recordOutward };
})();

// Auto-init on load
RMData.init();
