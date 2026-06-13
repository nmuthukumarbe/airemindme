/**
 * catalog-plans-products.js
 */

// ─────────────────────────────────────────────────────────────────────────────
// ╔══════════════════════════════════════════════════════════╗
// ║                         PLANS                           ║
// ╚══════════════════════════════════════════════════════════╝
// ─────────────────────────────────────────────────────────────────────────────
function loadPlans(page = 0) {
    api(`${API.plans}?page=${page}&size=6`)
        .then(data => {

            plans = data.content || [];

            planPage = data.number;
            planTotalPages = data.totalPages;
            planTotalElements = data.totalElements || 0;

            renderPlans();
            renderPlanPagination();
        })
        .catch(err =>
            showToast('Failed to load plans: ' + err.message, 'error')
        );
}

function renderPlans() {
    const q = (document.getElementById('planSearch')?.value || '').toLowerCase();
    const filtered = plans.filter(p =>
        (planFilter === 'all' || p.status === planFilter) &&
        ((p.name || '').toLowerCase().includes(q) || (p.description || '').toLowerCase().includes(q))
    );

    const countEl = document.getElementById('planTabCount');
    const badgeEl = document.getElementById('planCountBadge');
    if (countEl) countEl.textContent = planTotalElements;
    if (badgeEl) badgeEl.textContent = plans.filter(p => p.status === 'active').length;

    const grid = document.getElementById('plansGrid');
    const empty = document.getElementById('plansEmpty');

    if (!filtered.length) {
        grid.innerHTML = '';
        empty.classList.remove('hidden');
        return;
    }
    empty.classList.add('hidden');

    grid.innerHTML = filtered.map(p => `
    <div class=" mb-3 bg-white rounded-2xl border border-gray-100 shadow-sm p-5 hover:shadow-md transition relative group">
     ${p.imageUrl ? `
  <img src="${FILE_BASE}${p.imageUrl}" class="w-full h-40 object-cover rounded-xl mb-3 shadow-sm hover:shadow-lg transition ">` : `<img src="/assets/image.png" 
       class="w-full h-40 object-cover rounded-xl mb-3">`}
      <div class="flex items-start justify-between mb-3 mt-2"">
        <div class="flex items-center gap-3">
          <div class="w-9 h-9 rounded-xl bg-indigo-100 flex items-center justify-center flex-shrink-0">
            <svg class="w-5 h-5 text-indigo-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>
            </svg>
          </div>
          <div>
            <p class="text-sm font-bold text-gray-900">${p.name}</p>
            <div class="flex items-center gap-1.5 mt-0.5">
              <span class="text-sm font-bold text-indigo-600">${sym(p)}${Number(p.price || 0).toLocaleString()}</span>
              <span class="text-xs text-gray-400">${BILLING_LABELS[p.billingCycle] || p.billingCycle || ''}</span>
            </div>
          </div>
        </div>
        <div class="flex gap-1.5 opacity-0 group-hover:opacity-100 transition">
          <button onclick="openPlanModal(${p.id})" title="Edit"
            class="w-8 h-8 rounded-xl border border-gray-200 flex items-center justify-center text-gray-400 hover:text-indigo-600 hover:border-indigo-200 hover:bg-indigo-50">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/></svg>
          </button>
          <button onclick="togglePlanStatus(${p.id})" title="${p.status === 'active' ? 'Set Inactive' : 'Set Active'}"
            class="w-8 h-8 rounded-xl border border-gray-200 flex items-center justify-center ${p.status === 'active' ? 'text-emerald-500 hover:text-orange-500 hover:border-orange-200 hover:bg-orange-50' : 'text-gray-400 hover:text-emerald-500 hover:border-emerald-200 hover:bg-emerald-50'}">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="${p.status === 'active' ? 'M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 005.636 5.636m12.728 12.728L5.636 5.636' : 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'}"/></svg>
          </button>
          <button onclick="deletePlan(${p.id})" title="Delete"
            class="w-8 h-8 rounded-xl border border-gray-200 flex items-center justify-center text-gray-400 hover:text-red-500 hover:border-red-200 hover:bg-red-50">
            <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
          </button>
        </div>
      </div>
      ${p.description ? `<p class="text-xs text-gray-500 mb-3 leading-relaxed">${p.description}</p>` : ''}
      ${buildFeatureTags(p.features)}
      <div class="flex items-center justify-between mt-2 pt-3 border-t border-gray-50">
        <span class="text-[10px] text-gray-300">Created ${p.createdAt ? p.createdAt.split('T')[0] : ''}</span>
        <span class="px-2 py-0.5 rounded-full text-[10px] font-bold ${p.status === 'active' ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-400'}">${p.status === 'active' ? 'Active' : 'Inactive'}</span>
      </div>
    </div>`).join('');
}


function renderProductPagination() {
    const el = document.getElementById('productPagination');
    if (!el) return;

    el.innerHTML = `
        <button ${productPage === 0 ? 'disabled' : ''}
            onclick="loadProducts(${productPage - 1})">
            Prev
        </button>

        <span>Page ${productPage + 1} of ${productTotalPages}</span>

        <button ${productPage >= productTotalPages - 1 ? 'disabled' : ''}
            onclick="loadProducts(${productPage + 1})">
            Next
        </button>
    `;
}
function renderPlanPagination() {
    const el = document.getElementById('planPagination');
    if (!el) return;

    el.innerHTML = `
        <button
            ${planPage === 0 ? 'disabled' : ''}
            onclick="loadPlans(${planPage - 1})"
            class="px-3 py-2 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed">
            Prev
        </button>

        <span class="px-4 text-sm text-gray-600">
            Page ${planPage + 1} of ${planTotalPages}
        </span>

        <button
            ${planPage >= planTotalPages - 1 ? 'disabled' : ''}
            onclick="loadPlans(${planPage + 1})"
            class="px-3 py-2 border rounded-lg disabled:opacity-50 disabled:cursor-not-allowed">
            Next
        </button>
    `;
}
function buildFeatureTags(features) {
    let list = [];
    if (Array.isArray(features)) list = features;
    else if (typeof features === 'string' && features.trim())
        list = features.split('\n').map(s => s.trim()).filter(Boolean);
    if (!list.length) return '';
    return `<div class="flex flex-wrap gap-1.5 mb-3">${list.map(f =>
        `<span class="text-[11px] text-indigo-600 font-medium flex items-center gap-1"><svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2.5" d="M5 13l4 4L19 7"/></svg>${f}</span>`
    ).join('')}</div>`;
}

// ── Plan Modal ────────────────────────────────────────────────────────────────
function openPlanModal(id) {
    editPlanId = id || null;

    clearPlanImg();

    const previewBox = document.getElementById("planImgPreview");
    const uploadBox = document.getElementById("planImgZone");
    const img = document.getElementById("planImgThumb");

    if (editPlanId) {
        const p = plans.find(x => x.id === editPlanId);
        if (!p) return;

        // 🔹 SET IMAGE PREVIEW
        if (p.imageUrl) {
            img.src = FILE_BASE + p.imageUrl;

            previewBox.classList.remove("hidden");
            uploadBox.classList.add("hidden");
        } else {
            previewBox.classList.add("hidden");
            uploadBox.classList.remove("hidden");
        }

        // 🔹 SET FORM VALUES
        document.getElementById('pm_name').value = p.name || '';
        document.getElementById('pm_price').value = p.price || '';
        document.getElementById('pm_desc').value = p.description || '';
        document.getElementById('pm_currency').value = p.currency || 'INR';
        document.getElementById('pm_features').value =
            Array.isArray(p.features) ? p.features.join('\n') : (p.features || '');

        selectBilling(p.billingCycle || 'monthly');

        document.getElementById('planModalTitle').textContent = 'Edit Plan';
        document.getElementById('planSaveLabel').textContent = 'Save Changes';

    } else {
        // 🔹 RESET FORM (CREATE MODE)
        ['pm_name', 'pm_price', 'pm_desc', 'pm_features']
            .forEach(i => document.getElementById(i).value = '');

        document.getElementById('pm_currency').value = 'INR';
        selectBilling('monthly');

        previewBox.classList.add("hidden");
        uploadBox.classList.remove("hidden");

        document.getElementById('planModalTitle').textContent = 'New Plan';
        document.getElementById('planSaveLabel').textContent = 'Create Plan';
    }

    document.getElementById('planModal').classList.remove('hidden');

    if (editPlanId) {
        const p = plans.find(x => x.id === editPlanId);

        document.getElementById('pm_name').value = p.name || '';
        document.getElementById('pm_price').value = p.price || '';
        document.getElementById('pm_desc').value = p.description || '';
        document.getElementById('pm_currency').value = p.currency || 'INR';
        document.getElementById('pm_features').value = Array.isArray(p.features) ? p.features.join('\n') : (p.features || '');
        selectBilling(p.billingCycle || 'monthly');
        document.getElementById('planModalTitle').textContent = 'Edit Plan';
        document.getElementById('planSaveLabel').textContent = 'Save Changes';
    } else {
        ['pm_name', 'pm_price', 'pm_desc', 'pm_features'].forEach(i => document.getElementById(i).value = '');
        document.getElementById('pm_currency').value = 'INR';
        selectBilling('monthly');
        document.getElementById('planModalTitle').textContent = 'New Plan';
        document.getElementById('planSaveLabel').textContent = 'Create Plan';
    }
    document.getElementById('planModal').classList.remove('hidden');
}
function closePlanModal() { document.getElementById('planModal').classList.add('hidden'); }

function selectBilling(b) {
    currentBilling = b;
    ['monthly', 'quarterly', 'yearly', 'one-time'].forEach(x => {
        const btn = document.getElementById('bc-' + x);
        if (!btn) return;
        if (x === b) {
            btn.classList.add('bg-indigo-50', 'border-indigo-500', 'text-indigo-600');
            btn.classList.remove('border-gray-200', 'text-gray-600');
        } else {
            btn.classList.remove('bg-indigo-50', 'border-indigo-500', 'text-indigo-600');
            btn.classList.add('border-gray-200', 'text-gray-600');
        }
    });
}

function previewPlanImg(input) {
    if (input.files && input.files[0]) {
        const r = new FileReader();
        r.onload = e => {
            document.getElementById('planImgThumb').src = e.target.result;
            document.getElementById('planImgPreview').classList.remove('hidden');
            document.getElementById('planImgZone').classList.add('hidden');
        };
        r.readAsDataURL(input.files[0]);
    }
}
function clearPlanImg() {
    const inp = document.getElementById('planImgInput');
    if (inp) inp.value = '';
    document.getElementById('planImgPreview').classList.add('hidden');
    document.getElementById('planImgZone').classList.remove('hidden');
}
let isSavingPlan = false;

async function savePlan() {
    if (isSavingPlan) return;

    isSavingPlan = true;

    const btn = document.getElementById("planSaveBtn");

    // 🔹 Safe button handling
    if (btn) {
        btn.disabled = true;
        btn.innerText = "Saving...";
    }

    try {
        const name = document.getElementById('pm_name').value.trim();
        const price = document.getElementById('pm_price').value;

        if (!name || !price) {
            showToast('Plan name and price are required', 'error');
            return;
        }

        const file = document.getElementById("planImgInput").files[0];

        const payload = {
            name,
            billingCycle: currentBilling,
            currency: document.getElementById('pm_currency').value,
            price: parseFloat(price) || 0,
            description: document.getElementById('pm_desc').value.trim(),
            features: document.getElementById('pm_features').value.trim(),
            status: 'active'
        };

        let res;

        // 🔹 Create or Update
        if (editPlanId) {
            await api(`/api/catalog/plans/${editPlanId}`, 'PUT', payload);
            res = { id: editPlanId };
        } else {
            res = await api(`/api/catalog/plans`, 'POST', payload);
        }

        const planId = res.id;

        // 🔹 Upload Image
        if (file) {
            const formData = new FormData();
            formData.append("file", file);

            const uploadRes = await fetch(`/doc/upload/plan/${planId}`, {
                method: "POST",
                body: formData
            });

            const data = await uploadRes.json();

            await api(`/api/catalog/plans/${planId}/image`, 'PUT', {
                imageUrl: data.url
            });
        }

        showToast(editPlanId ? 'Plan updated!' : 'Plan created!', 'success');
        closePlanModal();
        loadPlans();

    } catch (err) {
        console.error(err);
        showToast('Error: ' + err.message, 'error');
    } finally {
        isSavingPlan = false;

        if (btn) {
            btn.disabled = false;
            btn.innerText = editPlanId ? "Save Changes" : "Create Plan";
        }
    }
}

function togglePlanStatus(id) {
    api(`${API.plans}/${id}/toggle-status`, 'PATCH')
        .then(() => { showToast('Plan status updated', 'success'); loadPlans(); })
        .catch(err => showToast('Error: ' + err.message, 'error'));
}
function deletePlan(id) {
    if (!confirm('Delete this plan?')) return;
    api(`${API.plans}/${id}`, 'DELETE')
        .then(() => { showToast('Plan deleted', 'success'); loadPlans(); })
        .catch(err => showToast('Error: ' + err.message, 'error'));
}

// ─────────────────────────────────────────────────────────────────────────────
// ╔══════════════════════════════════════════════════════════╗
// ║                       PRODUCTS                          ║
// ╚══════════════════════════════════════════════════════════╝
// ─────────────────────────────────────────────────────────────────────────────
function loadProducts(page = 0) {
    api(`${API.products}?page=${page}&size=6`)
        .then(data => {

            products = data.content || [];

            productPage = data.number;
            productTotalPages = data.totalPages;
            productTotalElements = data.totalElements || 0;

            renderProducts();
            renderProductPagination();
        })
        .catch(err =>
            showToast('Failed to load products: ' + err.message, 'error')
        );
}

function renderProducts() {
    const q = (document.getElementById('prodSearch')?.value || '').toLowerCase();
    const filtered = products.filter(p =>
        (prodFilter === 'all' || p.status === prodFilter) &&
        ((p.name || '').toLowerCase().includes(q) ||
            (p.sku || '').toLowerCase().includes(q) ||
            (p.category || '').toLowerCase().includes(q))
    );

    const countEl = document.getElementById('prodTabCount');
    const badgeEl = document.getElementById('productCountBadge');
    if (countEl) countEl.textContent = productTotalElements;
    if (badgeEl) badgeEl.textContent = products.filter(p => p.status === 'active').length;

    const grid = document.getElementById('productsGrid');
    const empty = document.getElementById('productsEmpty');

    if (!filtered.length) { grid.innerHTML = ''; empty.classList.remove('hidden'); return; }
    empty.classList.add('hidden');

    const catColors = {
        Investments: 'bg-blue-50 text-blue-700',
        Insurance: 'bg-emerald-50 text-emerald-700',
        'Fixed Income': 'bg-violet-50 text-violet-700',
        Loans: 'bg-orange-50 text-orange-700',
        'Gold & Jewellery': 'bg-yellow-50 text-yellow-700',
        General: 'bg-gray-100 text-gray-600'
    };

    grid.innerHTML = filtered.map(p => {
        const catCls = catColors[p.category] || 'bg-gray-100 text-gray-600';
        const imgSrc = p.imageUrl ? FILE_BASE + p.imageUrl : "/assets/image.png"; // fallback
        return `
    <div  class="bg-white rounded-2xl border border-gray-100 shadow-sm p-5 hover:shadow-md transition relative group">
    <img 
  src="${p.imageUrl ? FILE_BASE + p.imageUrl : '/assets/image.png'}"
  class="w-full h-40 object-cover"
  onerror="this.src='/assets/image.png'"
>
      <div class="flex justify-end gap-1.5 absolute top-4 right-4 opacity-0 group-hover:opacity-100 transition">
        <button onclick="openProductModal(${p.id})" title="Edit"
          class="w-8 h-8 rounded-xl border border-gray-200 flex items-center justify-center text-gray-400 hover:text-indigo-600 hover:border-indigo-200 hover:bg-indigo-50">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15.232 5.232l3.536 3.536m-2.036-5.036a2.5 2.5 0 113.536 3.536L6.5 21.036H3v-3.572L16.732 3.732z"/></svg>
        </button>
        <button onclick="toggleProductStatus(${p.id})" title="${p.status === 'active' ? 'Set Inactive' : 'Set Active'}"
          class="w-8 h-8 rounded-xl border border-gray-200 flex items-center justify-center ${p.status === 'active' ? 'text-emerald-500 hover:text-orange-500 hover:border-orange-200 hover:bg-orange-50' : 'text-gray-400 hover:text-emerald-500 hover:border-emerald-200 hover:bg-emerald-50'}">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="${p.status === 'active' ? 'M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636' : 'M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z'}"/></svg>
        </button>
        <button onclick="deleteProduct(${p.id})" title="Delete"
          class="w-8 h-8 rounded-xl border border-gray-200 flex items-center justify-center text-gray-400 hover:text-red-500 hover:border-red-200 hover:bg-red-50">
          <svg class="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"/></svg>
        </button>
      </div>
      <div class="mb-3">
        <div class="w-9 h-9 rounded-xl bg-violet-100 flex items-center justify-center mb-2.5">
          <svg class="w-5 h-5 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>
        </div>
        <div class="flex items-center gap-2 flex-wrap">
          <p class="text-sm font-bold text-gray-900">${p.name}</p>
          <span class="text-[10px] font-mono text-gray-400">${p.sku}</span>
          <span class="px-2 py-0.5 rounded-full text-[10px] font-semibold ${catCls}">${p.category || 'General'}</span>
        </div>
        <p class="text-base font-bold text-indigo-600 mt-1">${sym(p)}${Number(p.price || 0).toLocaleString()}</p>
        <div class="mt-3 inline-flex items-center gap-2 px-3 py-2 rounded-xl bg-gray-50 border border-gray-100">
    <svg class="w-4 h-4 text-indigo-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
            d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />
    </svg>
    <span class="text-xs text-gray-500 font-medium">Stock</span>
    <span class="text-base font-bold text-gray-900">
        ${p.quantity || 0}
    </span>
</div>
<a href="/product-details/${p.id}"
   class="px-3 py-1.5 rounded-lg bg-indigo-600 text-white text-xs font-semibold">
   View Details
</a>
        ${p.description ? `<p class="text-xs text-gray-500 mt-1.5 leading-relaxed">${p.description}</p>` : ''}
      </div>
      <div class="flex items-center justify-between pt-3 border-t border-gray-50">
        <span class="text-[10px] text-gray-300">Created ${p.createdAt ? p.createdAt.split('T')[0] : ''}</span>
        <span class="px-2 py-0.5 rounded-full text-[10px] font-bold ${p.status === 'active' ? 'bg-emerald-50 text-emerald-600' : 'bg-gray-100 text-gray-400'}">${p.status === 'active' ? 'Active' : 'Inactive'}</span>
      </div>
    </div>`;
    }).join('');
}

// ── Product Modal ─────────────────────────────────────────────────────────────
function openProductModal(id) {
    editProductId = id || null;
    clearProdImg();
    if (editProductId) {
        const p = products.find(x => x.id === editProductId);
        const previewBox = document.getElementById("prodImgPreview");
        const uploadBox = document.getElementById("prodImgZone");
        const img = document.getElementById("prodImgThumb");

        if (p.imageUrl) {
            img.src = FILE_BASE + p.imageUrl;

            previewBox.classList.remove("hidden");
            uploadBox.classList.add("hidden");
        }
        if (!p) return;
        document.getElementById('prd_name').value = p.name || '';
        document.getElementById('prd_sku').value = p.sku || '';
        document.getElementById('prd_price').value = p.price || '';
        document.getElementById('prd_currency').value = p.currency || 'INR';
        document.getElementById('prd_category').value = p.category || 'General';
        document.getElementById('prd_desc').value = p.description || ''; currentQuantity = p.quantity || 0;
        document.getElementById('prd_current_qty').value = currentQuantity;
        document.getElementById('prd_quantity').value = 0;
        document.getElementById('productModalTitle').textContent = 'Edit Product';
        document.getElementById('prodSaveLabel').textContent = 'Save Changes';
    } else {
        ['prd_name', 'prd_sku', 'prd_price', 'prd_desc', 'prd_quantity'].forEach(i => document.getElementById(i).value = '');
        document.getElementById('prd_currency').value = 'INR';
        document.getElementById('prd_category').value = 'Investments';
        currentQuantity = 0;
        document.getElementById('prd_current_qty').value = 0;
        document.getElementById('prd_quantity').value = 0;
        document.getElementById('productModalTitle').textContent = 'New Product';
        document.getElementById('prodSaveLabel').textContent = 'Create Product';
    }
    document.getElementById('productModal').classList.remove('hidden');
}
function closeProductModal() { document.getElementById('productModal').classList.add('hidden'); }

function previewProdImg(input) {
    if (input.files && input.files[0]) {
        const r = new FileReader();
        r.onload = e => {
            document.getElementById('prodImgThumb').src = e.target.result;
            document.getElementById('prodImgPreview').classList.remove('hidden');
            document.getElementById('prodImgZone').classList.add('hidden');
        };
        r.readAsDataURL(input.files[0]);
    }
}
function clearProdImg() {
    const inp = document.getElementById('prodImgInput');
    if (inp) inp.value = '';
    document.getElementById('prodImgPreview')?.classList.add('hidden');
    document.getElementById('prodImgZone')?.classList.remove('hidden');
}
let isSavingProduct = false;
async function saveProduct() {

    if (isSavingProduct) return;

    isSavingProduct = true;

    try {
        const name = document.getElementById('prd_name').value.trim();
        const sku = document.getElementById('prd_sku').value.trim();

        if (!name || !sku) {
            showToast('Product name and SKU are required', 'error');

            btn.disabled = false;
            isSavingProduct = false;
            btn.innerText = "Save Product";

            return;
        }
        const file = document.getElementById("prodImgInput").files[0];
        const addQty = parseInt(document.getElementById('prd_quantity').value || 0);
        const finalQty = editProductId ? currentQuantity + addQty : addQty;

        const payload = {
            name,
            sku,
            category: document.getElementById('prd_category').value,
            currency: document.getElementById('prd_currency').value,
            price: parseFloat(document.getElementById('prd_price').value) || 0,
            quantity: finalQty,
            description: document.getElementById('prd_desc').value.trim(),
            status: 'active'
        };

        let res;

        // 🔹 Create or Update
        if (editProductId) {
            await api(`/api/catalog/products/${editProductId}`, 'PUT', payload);
            res = { id: editProductId };
        } else {
            res = await api(`/api/catalog/products`, 'POST', payload);
        }

        const productId = res.id;

        // 🔹 Upload image
        if (file) {
            const formData = new FormData();
            formData.append("file", file);

            const uploadRes = await fetch(`/doc/upload/product/${productId}`, {
                method: "POST",
                body: formData
            });

            const data = await uploadRes.json();

            await api(`/api/catalog/products/${productId}/image`, 'PUT', {
                imageUrl: data.url
            });
        }

        showToast(editProductId ? 'Product updated!' : 'Product created!', 'success');
        closeProductModal();
        loadProducts();

    } catch (err) {
        console.error(err);
        showToast('Error: ' + err.message, 'error');
    } isSavingProduct = false;
}

function toggleProductStatus(id) {
    api(`${API.products}/${id}/toggle-status`, 'PATCH')
        .then(() => { showToast('Product status updated', 'success'); loadProducts(); })
        .catch(err => showToast('Error: ' + err.message, 'error'));
}
function deleteProduct(id) {
    if (!confirm('Delete this product?')) return;
    api(`${API.products}/${id}`, 'DELETE')
        .then(() => { showToast('Product deleted', 'success'); loadProducts(); })
        .catch(err => showToast('Error: ' + err.message, 'error'));
}
