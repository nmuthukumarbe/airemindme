/**
 * catalog-common.js
 */

'use strict';

// ─────────────────────────────────────────────────────────────────────────────
// CONSTANTS
// ─────────────────────────────────────────────────────────────────────────────
const API = {
    plans: '/api/catalog/plans',
    products: '/api/catalog/products',
    templates: '/api/catalog/templates',
    rtemplates: '/api/catalog/rtemplates',
    summary: '/api/catalog/summary'
};
const FILE_BASE = "/doc/view?path=";
const BILLING_LABELS = { monthly: 'Monthly', quarterly: 'Quarterly', yearly: 'Yearly', 'one-time': 'One-Time' };
const CURRENCY_SYMBOLS = { INR: '₹', USD: '$', EUR: '€', GBP: '£', AED: 'AED ', SGD: 'S$' };

// ─────────────────────────────────────────────────────────────────────────────
// STATE
// ─────────────────────────────────────────────────────────────────────────────
let plans = [];
let products = [];
let templates = [];
let rtemplates = [];

let planFilter = 'all';
let prodFilter = 'all';
let tmplFilter = 'all';

let editPlanId = null;   // null = create, number = edit
let editProductId = null;
//calcaulate ADD QUANTITY KU 
let currentQuantity = 0;
let editTemplateId = null;
let editRTId = null;
let planPage = 0;
let productPage = 0;

let planTotalPages = 0;
let productTotalPages = 0;

let planTotalElements = 0;
let productTotalElements = 0;

let currentBilling = 'monthly';
let rtCols = [];     // [{ uid, value }]

// ─────────────────────────────────────────────────────────────────────────────
// UTILITIES
// ─────────────────────────────────────────────────────────────────────────────
function sym(entity) {
    return CURRENCY_SYMBOLS[entity.currency] || '₹';
}

/** Generic fetch wrapper — always sends JSON, always parses JSON */
async function api(url, method = 'GET', body = null) {
    const opts = {
        method,
        headers: { 'Content-Type': 'application/json' }
    };
    if (body !== null) opts.body = JSON.stringify(body);

    const res = await fetch(url, opts);
    if (!res.ok) {
        const text = await res.text().catch(() => res.statusText);
        throw new Error(`${res.status}: ${text}`);
    }
    // DELETE returns 200 with empty body
    const text = await res.text();
    return text ? JSON.parse(text) : null;
}

// ─────────────────────────────────────────────────────────────────────────────
// TOAST
// ─────────────────────────────────────────────────────────────────────────────
function showToast(msg, type = 'success') {
    let container = document.getElementById('toastContainer');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'fixed bottom-24 md:bottom-6 right-4 z-[100] flex flex-col gap-2';
        document.body.appendChild(container);
    }
    const bg = { success: 'bg-emerald-600', error: 'bg-red-500', info: 'bg-indigo-600' }[type] || 'bg-gray-800';
    const icon = type === 'error'
        ? 'M6 18L18 6M6 6l12 12'
        : 'M5 13l4 4L19 7';
    const toast = document.createElement('div');
    toast.className = `${bg} text-white text-sm font-medium px-4 py-3 rounded-2xl shadow-lg flex items-center gap-2 fade-in`;
    toast.innerHTML = `<svg class="w-4 h-4 flex-shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="${icon}"/></svg>${msg}`;
    container.appendChild(toast);
    setTimeout(() => toast.remove(), 3000);
}

// ─────────────────────────────────────────────────────────────────────────────
// MOBILE NAV
// ─────────────────────────────────────────────────────────────────────────────
function openMobileNav() {
    document.getElementById('mobileNavDrawer').style.transform = 'translateX(0)';
    document.getElementById('mobileNavBg').classList.remove('hidden');
    document.body.style.overflow = 'hidden';
}
function closeMobileNav() {
    document.getElementById('mobileNavDrawer').style.transform = 'translateX(-100%)';
    document.getElementById('mobileNavBg').classList.add('hidden');
    document.body.style.overflow = '';
}

// ─────────────────────────────────────────────────────────────────────────────
// TAB SWITCHING
// ─────────────────────────────────────────────────────────────────────────────
function moveIndicator(el) {
    const ind = document.getElementById('tabIndicator');
    if (!ind || !el) return;
    ind.style.width = el.offsetWidth + 'px';
    ind.style.transform = `translateX(${el.offsetLeft}px)`;
}

function switchCatalogTab(tab) {
    ['plans', 'products', 'templates', 'rtemplates'].forEach(t => {
        const btn = document.getElementById('ct-' + t);
        const panel = document.getElementById('cp-' + t);
        if (panel) panel.classList.toggle('hidden', t !== tab);
        if (!btn) return;
        if (t === tab) { btn.classList.add('text-indigo-600'); btn.classList.remove('text-gray-500'); moveIndicator(btn); }
        else { btn.classList.remove('text-indigo-600'); btn.classList.add('text-gray-500'); }
    });
}

// ─────────────────────────────────────────────────────────────────────────────
// FILTER HELPERS
// ─────────────────────────────────────────────────────────────────────────────
function setPlanFilter(f) {
    planFilter = f;
    ['all', 'active', 'inactive'].forEach(x => {
        const b = document.getElementById('pf-' + x);
        if (!b) return;
        b.className = x === f
            ? 'px-4 py-2 text-sm font-semibold bg-indigo-600 text-white'
            : 'px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50';
    });
    renderPlans();
}
function setProdFilter(f) {
    prodFilter = f;
    ['all', 'active', 'inactive'].forEach(x => {
        const b = document.getElementById('prf-' + x);
        if (!b) return;
        b.className = x === f
            ? 'px-4 py-2 text-sm font-semibold bg-indigo-600 text-white'
            : 'px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50';
    });
    renderProducts();
}
function setTmplFilter(f) {
    tmplFilter = f;
    ['all', 'active', 'inactive'].forEach(x => {
        const b = document.getElementById('tf-' + x);
        if (!b) return;
        b.className = x === f
            ? 'px-4 py-2 text-sm font-semibold bg-emerald-600 text-white'
            : 'px-4 py-2 text-sm font-semibold text-gray-600 hover:bg-gray-50';
    });
    renderTemplates();
}

// ─────────────────────────────────────────────────────────────────────────────
// SUMMARY (header badges from server)
// ─────────────────────────────────────────────────────────────────────────────
function loadSummary() {
    api(API.summary)
        .then(data => {
            const planBadge = document.getElementById('planCountBadge');
            const prodBadge = document.getElementById('productCountBadge');
            if (planBadge) planBadge.textContent = data.activePlans || 0;
            if (prodBadge) prodBadge.textContent = data.activeProducts || 0;
        })
        .catch(() => { /* non-critical — individual loaders update their own badges */ });
}
