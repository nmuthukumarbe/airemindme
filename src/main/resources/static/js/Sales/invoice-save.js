//Actions

async function saveInvoice(invoicePayload) {
    try {
        // Determine if this is a new or existing invoice
        const isNew = !invoicePayload.id;
        const method = isNew ? "POST" : "PUT";
        const url = isNew ? "/api/invoices" : `/api/invoices/${invoicePayload.id}`;

        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(invoicePayload)
        });

        if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText);
        }

        const saved = await response.json();
        console.log("Invoice saved:", saved);

        // Clear preview data
        localStorage.removeItem("invoice_preview");

        showToast(`Invoice saved successfully`);
        return saved;

    } catch (err) {
        console.error("Save Error:", err);
        showToast(`Failed to save: ${err.message}`);
        throw err;
    }
}
function buildInvoicePayload() {
    const params = new URLSearchParams(location.search);
    const invoiceId = params.get("id");

    const items = [];

    document.querySelectorAll(".invoice-row").forEach(row => {
        items.push({
            itemType: row.querySelector(".itemType")?.value,
            itemRefId: row.querySelector(".itemRefId")?.value,
            itemName: row.querySelector(".itemName")?.value,
            description: row.querySelector(".itemDescription")?.value || "",
            hsnSac: row.querySelector(".hsnSac")?.value || "",
            qty: parseInt(row.querySelector(".qty")?.value || 0),
            rate: parseFloat(row.querySelector(".rate")?.value || 0),
            gst: parseFloat(row.querySelector(".gst")?.value || 0),
            taxAmount: parseFloat(row.querySelector(".taxAmount")?.value || 0),
            lineTotal: parseFloat(row.querySelector(".lineAmount")?.value || 0)
        });
    });

    const payload = {
        customerId: selectedCustomerId,
        customerName: selectedCustomer?.name || "",
        customerAddress: document.getElementById("billToAddr")?.value || "",
        customerPhone: selectedCustomer?.mobile || "",
        customerGst: document.getElementById("billToGST")?.value || "",
        shippingAddress: document.getElementById("shipToAddr")?.value || "",
        invoiceNumber: document.getElementById("invoiceNum").value,
        invoiceDate: document.getElementById("invoiceDate").value,
        dueDate: document.getElementById("dueDate").value,
        subtotal: extractMoney("subtotalDisplay"),
        discountAmount: parseFloat(document.getElementById("discountVal")?.value || 0),
        taxAmount: extractMoney("cgstDisplay") + extractMoney("sgstDisplay"),
        shippingAmount: parseFloat(document.getElementById("shippingAmt").value || 0),
        grandTotal: extractMoney("totalDisplay"),
        notes: document.getElementById("invoiceNotes").value,
        terms: document.getElementById("invoiceTerms").value,
        items
    };

    // If editing, include the ID
    if (invoiceId) {
        payload.id = invoiceId;
    }
    console.log("PAYLOAD =>", payload);

    return payload;
}

function extractMoney(id) {

    return parseFloat(
        document.getElementById(id)
            .textContent
            .replace(/[₹,]/g, '')
    ) || 0;
}


const preview = JSON.parse(localStorage.getItem("invoice_preview"));

if (preview) {
    renderInvoice(preview);
}
function previewInvoice() {

    const invoice = buildInvoicePayload();

    localStorage.setItem(
        "invoice_preview",
        JSON.stringify(invoice)
    );

    window.open(
        "/invoice-detail.html?preview=true",
        "_blank"
    );
}


async function saveAndContinue() {
    const invoice = buildInvoicePayload();
    invoice.status = "DRAFT";

    try {
        const saved = await saveInvoice(invoice);
        // Redirect to the same invoice for continued editing
        setTimeout(() => {
            location.href = `/invoice-create.html?id=${saved.id}&edit=true`;
        }, 500);
    } catch (err) {
        console.error("Save and continue failed:", err);
    }
}

/**
 * Save invoice as UNPAID (final save for new invoices)
 */
async function saveFinalInvoice() {
    const invoice = buildInvoicePayload();
    invoice.status = "SENT";

    try {
        await saveInvoice(invoice);

        showToast("Invoice sent successfully");

        setTimeout(() => {
            location.href = "/sales.html";
        }, 500);

    } catch (err) {
        console.error("Save invoice failed:", err);
        showToast(`Failed to save: ${err.message}`);
    }
}

async function saveDraft() {
    const invoice = buildInvoicePayload();
    invoice.status = "DRAFT";

    try {
        await saveInvoice(invoice);

        showToast("Draft saved successfully");

        setTimeout(() => {
            location.href = "/sales.html";
        }, 500);

    } catch (err) {
        console.error("Save draft failed:", err);
        showToast(`Failed to save: ${err.message}`);
    }
}