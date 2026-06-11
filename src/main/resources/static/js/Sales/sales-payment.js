async function renderPayments() {
 
    const paymentsList = document.getElementById('paymentsList');
 console.log("renderPayments called");
    paymentsList.innerHTML = `
        <div class="p-6 text-center text-gray-500">
            Loading payments...
        </div>
    `;

    try {

        const response = await fetch('/api/payments/list');
      

        const payments = await response.json();

        if (!payments.length) {
            paymentsList.innerHTML = `
                <div class="p-8 text-center text-gray-500">
                    No payments found
                </div>
            `;
            return;
        }

        paymentsList.innerHTML = payments.map(p => `
            <div class="flex items-center justify-between px-5 py-4 hover:bg-gray-50">

                <div class="flex items-center gap-3">

                    <div class="w-8 h-8 rounded-full bg-green-100 flex items-center justify-center">
                        ✓
                    </div>

                    <div>
                        <p class="text-sm font-semibold text-gray-800">
                            ${p.customerName || '-'}
                        </p>

                        <p class="text-xs text-gray-400">
                            ${p.invoiceNumber || '-'}
                            ·
                            ${p.paymentDate || '-'}
                            ·
                            ${p.paymentMode || '-'}
                        </p>

                        <p class="text-xs text-gray-400">
                            Ref: ${p.referenceNo || '-'}
                        </p>
                    </div>

                </div>

                <p class="text-sm font-bold text-green-600">
                    ₹${Number(p.amount || 0).toLocaleString('en-IN')}
                </p>

            </div>
        `).join('');

    } catch (e) {

        console.error(e);

        paymentsList.innerHTML = `
            <div class="p-8 text-center text-red-500">
                Failed to load payments
            </div>
        `;
    }
}
// }
// async function loadPaymentDashboard() {

//     try {

//         const response =
//             await fetch('/api/payments/dashboard');

//         const data = await response.json();

//         document.getElementById('totalCollected').textContent =
//             '₹' + Number(data.totalCollected || 0)
//                 .toLocaleString('en-IN');

//         document.getElementById('pendingReceivables').textContent =
//             '₹' + Number(data.pendingReceivables || 0)
//                 .toLocaleString('en-IN');

//         document.getElementById('partialAmount').textContent =
//             '₹' + Number(data.partiallyPaidAmount || 0)
//                 .toLocaleString('en-IN');

//         document.getElementById('overdueAmount').textContent =
//             '₹' + Number(data.overdueAmount || 0)
//                 .toLocaleString('en-IN');

//     } catch (e) {
//         console.error(e);
//     }
// }


document.addEventListener('DOMContentLoaded', () => {

    // loadPaymentDashboard();

    renderPayments();

});

async function loadPayments() {

    const res = await fetch('/api/payments/list');
    const payments = await res.json();

    const container =
        document.getElementById('paymentsList');

    container.innerHTML = payments.map(p => `
        <div class="flex items-center justify-between p-4 border-b">
            <div>
                <p class="font-semibold">
                    ${p.customerName || '-'}
                </p>

                <p class="text-xs text-gray-500">
                    ${p.invoiceNumber || '-'}
                </p>

                <p class="text-xs text-gray-400">
                    ${p.paymentDate}
                </p>
            </div>

            <div class="text-right">
                <p class="font-bold text-green-600">
                    ₹${Number(p.amount).toLocaleString('en-IN')}
                </p>

                <p class="text-xs text-gray-500">
                    ${p.paymentMode}
                </p>
            </div>
        </div>
    `).join('');
}