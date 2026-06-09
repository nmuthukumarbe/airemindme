let selectedCustomerId = null;
//Customer Search & Selection

async function searchCustomer(keyword) {

    const dd = document.getElementById("customerDd");

    if (!keyword || keyword.trim().length < 1) {
        dd.innerHTML = "";
        dd.classList.add("hidden");
        return;
    }

    try {

        const response = await fetch(
            `/api/customers/search?query=${encodeURIComponent(keyword)}`
        );

        if (!response.ok) {
            throw new Error("Failed to search customers");
        }

        const customers = await response.json();

        if (!customers || customers.length === 0) {

            dd.innerHTML = `
                <div class="px-3 py-2 text-sm text-gray-500">
                    No customers found
                </div>
            `;

            dd.classList.remove("hidden");
            return;
        }

        dd.innerHTML = customers.map(c => `
            <div
                class="px-3 py-2 hover:bg-gray-50 cursor-pointer border-b border-gray-100"
                onclick="selectCustomer(${c.id})">

                <div class="font-medium text-sm">
                    ${c.name}
                </div>

                <div class="text-xs text-gray-500">
                    #${c.id} • ${c.mobile || ''}
                </div>
            </div>
        `).join("");

        dd.classList.remove("hidden");

    } catch (err) {

        console.error(err);

        dd.innerHTML = `
            <div class="px-3 py-2 text-sm text-red-500">
                Error loading customers
            </div>
        `;

        dd.classList.remove("hidden");
    }
}

async function selectCustomer(customerId) {

    try {

        const response = await fetch(
            `/api/customers/${customerId}`
        );

        if (!response.ok) {
            throw new Error("Customer not found");
        }

        const customer = await response.json();

        selectedCustomerId = customer.id;

        document.getElementById("customerSearch").value =
            customer.name || "";

        document.getElementById("billToAddr").value =
            customer.address || "";

        const gstField = document.getElementById("billToGST");

        if (gstField) {
            gstField.value = customer.gstNumber || "";
        }

        closeCustomerDd();

    } catch (err) {
        console.error(err);
        alert("Unable to load customer details");
    }
}
// Close dropdown
function closeCustomerDd() {

    const dd = document.getElementById("customerDd");

    dd.classList.add("hidden");
}
// Same as billing address
function toggleShipTo(cb) {

    const billAddr =
        document.getElementById("billToAddr").value;

    const shipAddr =
        document.getElementById("shipToAddr");

    if (cb.checked) {
        shipAddr.value = billAddr;
        shipAddr.setAttribute("readonly", true);
    } else {
        shipAddr.removeAttribute("readonly");
    }
}
// ===========================
// Add item to invoice
// ===========================


let searchTimer;
function debounceSearch(fn, delay = 300) {
    clearTimeout(searchTimer);
    searchTimer = setTimeout(fn, delay);
}

/**
 * REUSABLE SEARCH UTILITY
 */
async function performSearch(input, endpoint, selectHandler) {
    const keyword = input.value.trim();
    const dd = input.parentElement.querySelector(".itemDropdown");

    if (!dd) {
        console.error("Dropdown element not found");
        return;
    }

    if (keyword.length < 1) {
        dd.classList.add("hidden");
        return;
    }

    try {
        const res = await fetch(`/api/catalog/${endpoint}/search?query=${encodeURIComponent(keyword)}`);
        const items = await res.json();

        if (!items.length) {
            dd.innerHTML = `<div class="px-3 py-2 text-sm text-gray-500">No ${endpoint} found</div>`;
        } else {
            dd.innerHTML = items.map(i => {
                // Using i.installmentAmount for plans, i.price for products
                const displayPrice = i.price || i.installmentAmount || 0;
                return `
                    <div class="px-3 py-2 hover:bg-gray-100 cursor-pointer border-b text-sm"
                         onclick="${selectHandler}(this, ${i.id}, '${escapeHtml(i.name)}', ${displayPrice})">
                        <div class="font-medium">${i.name}</div>
                        <div class="text-xs text-gray-500">₹${displayPrice}</div>
                    </div>
                `;
            }).join("");
        }
        dd.classList.remove("hidden");
    } catch (e) {
        console.error(`Search failed for ${endpoint}:`, e);
    }
}

// Wrapper functions
async function searchProduct(input) { await performSearch(input, 'products', 'selectProduct'); }
async function searchPlan(input) { await performSearch(input, 'plans', 'selectPlan'); }

/**
 * ITEM SELECTION LOGIC
 */
function handleSelection(element, id, name, price, type) {
    const row = element.closest(".invoice-row");
    if (!row) return;

    row.querySelector(".itemName").value = name;
    row.querySelector(".itemType").value = type;
    row.querySelector(".itemRefId").value = id;
    row.querySelector(".qty").value = 1;
    row.querySelector(".rate").value = price;

    calculateRow(row);
    closeDropdown(row);
}

function selectProduct(el, id, name, price) { handleSelection(el, id, name, price, 'PRODUCT'); }
function selectPlan(el, id, name, amount) { handleSelection(el, id, name, amount, 'SERVICE'); }

/**
 * CALCULATION LOGIC
 */
function calculateRow(row) {

    const qty = parseFloat(
        row.querySelector(".qty")?.value || 0
    );

    const rate = parseFloat(
        row.querySelector(".rate")?.value || 0
    );

    const gst = parseFloat(
        row.querySelector(".gst")?.value || 0
    );

    const subtotal = qty * rate;
    const tax = (subtotal * gst) / 100;
    const total = subtotal + tax;

    const taxField = row.querySelector(".taxAmount");
    const amountField = row.querySelector(".lineAmount");

    if (taxField) {
        taxField.value = tax.toFixed(2);
    }

    if (amountField) {
        amountField.value = total.toFixed(2);
    }

    calculateInvoiceTotal();
}

function calculateInvoiceTotal() {
    if (typeof calcTotals === "function") {
        calcTotals();
    }
}

/**
 * HELPERS
 */
function closeDropdown(row) {
    const dd = row.querySelector(".itemDropdown");
    if (dd) dd.classList.add("hidden");
}

function escapeHtml(text) {
    if (!text) return "";
    return String(text)
        .replace(/'/g, "\\'")
        .replace(/"/g, '&quot;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}


function addItem(type) {
    const container = document.getElementById("lineItems");
    const id = "item_" + Date.now();

    const isProduct = type === "product";
    const typeLabel = isProduct
        ? '<span class="text-xs text-indigo-600 font-semibold">PRODUCT</span>'
        : '<span class="text-xs text-purple-600 font-semibold">SERVICE</span>';

    const searchFunction = isProduct ? "searchProduct(this)" : "searchPlan(this)";

    const div = document.createElement("div");
    div.className = "invoice-row";

    div.innerHTML = `
    <div id="${id}" class="bg-gray-50 rounded-xl p-3 mb-2 border border-gray-100">
        
        <div class="flex items-start gap-2 mb-2">
            ${typeLabel}
            
            <div class="flex-1 relative">
                <input 
                    type="text" 
                    placeholder="Search & select ${type}..." 
                    class="line-input itemName" 
                    oninput="debounceSearch(() => ${searchFunction})">
                
                <div class="itemDropdown product-dropdown hidden"></div>
                
                <input type="hidden" class="itemType">
                <input type="hidden" class="itemRefId">
            </div>

            <button 
                type="button" 
                onclick="this.closest('.invoice-row').remove(); calculateInvoiceTotal();"
                class="w-8 h-8 flex items-center justify-center rounded-lg text-gray-400 hover:text-red-500 hover:bg-red-50 shrink-0">
                <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/>
                </svg>
            </button>
        </div>

        <input 
            type="text" 
            placeholder="Description (optional)" 
            class="line-input mb-2 text-xs text-gray-500">

        <div class="grid grid-cols-2 md:grid-cols-[1fr_70px_110px_80px_110px_100px] gap-2 items-center">
            <input type="text" placeholder="HSN/SAC" class="line-input text-xs">
            
            <input 
                type="number" value="1" min="1" 
                class="line-input text-center qty" 
                oninput="calculateRow(this.closest('.invoice-row'))">
            
            <input 
                type="number" value="0" min="0" 
                class="line-input text-right rate" 
                oninput="calculateRow(this.closest('.invoice-row'))">
            
            <select 
                class="line-input text-xs gst" 
                onchange="calculateRow(this.closest('.invoice-row'))">
                <option value="0">0%</option>
                <option value="5">5%</option>
                <option value="12">12%</option>
                <option value="18" selected>18%</option>
                <option value="28">28%</option>
            </select>

            <input 
                type="text" readonly 
                class="line-input text-right bg-gray-100 text-gray-500 taxAmount" 
                value="0.00">

            <input 
                type="text" readonly 
                class="line-input text-right bg-gray-100 font-semibold text-indigo-700 lineAmount" 
                value="0.00">
        </div>
    </div>`;

    container.appendChild(div);
}

// Helper to remove a row
function removeRow(rowId) {
    document.getElementById(rowId).remove();
    calculateInvoiceTotal();
}
function calcTotals() {

    let subtotal = 0;
    let totalTax = 0;

    document.querySelectorAll(".invoice-row").forEach(row => {

        const qty =
            parseFloat(row.querySelector(".qty")?.value || 0);

        const rate =
            parseFloat(row.querySelector(".rate")?.value || 0);

        const gst =
            parseFloat(row.querySelector(".gst")?.value || 0);

        const base = qty * rate;
        const tax = base * gst / 100;

        subtotal += base;
        totalTax += tax;
    });

    const discountVal =
        parseFloat(document.getElementById("discountVal")?.value || 0);

    const discountType =
        document.getElementById("discountType")?.value || "pct";

    const discount =
        discountType === "pct"
            ? (subtotal * discountVal / 100)
            : discountVal;

    const shipping =
        parseFloat(document.getElementById("shippingAmt")?.value || 0);

    const total =
        subtotal - discount + totalTax + shipping;

    const cgst = totalTax / 2;
    const sgst = totalTax / 2;

    document.getElementById("subtotalDisplay").textContent =
        "₹" + subtotal.toFixed(2);

    document.getElementById("cgstDisplay").textContent =
        "₹" + cgst.toFixed(2);

    document.getElementById("sgstDisplay").textContent =
        "₹" + sgst.toFixed(2);

    document.getElementById("totalDisplay").textContent =
        "₹" + total.toFixed(2);

    document.getElementById("amountInWords").textContent =
        "Amount in words: " +
        numberToWords(Math.round(total)) +
        " Rupees Only";
}