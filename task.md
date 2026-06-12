# Task Checklist

## Phase 1: Invoice Detail Page & Payment Validations
- [x] Modify `invoice-detail.html`
  - [x] Add ID `paymentSummaryCard` to the Payment Summary container
  - [x] Update `applyPreviewModeUI()` to support both preview mode and saved mode rules based on status
  - [x] Update redirects in `saveInvoiceData()` after Save Draft to point to `invoice-detail.html` with query parameters rather than `invoice-create.html`
  - [x] Update edit draft behavior in `editInvoice()` to ensure proper query parameters are used
  - [x] Add client-side validation logic in `recordPayment()` with specific error messages for empty amount, amount <= 0, amount exceeding balance, missing date, future date, network failure, and backend errors
- [x] Modify `SalesController.java`
  - [x] Update `/api/invoices/{id}/payments` POST handler to validate payment fields: empty amount, amount <= 0, missing date, future date, and payment amount exceeding remaining balance

## Phase 2: Payments Page Enhancement & Fixes
- [x] Enhance `sales.html`
  - [x] Create 6 top metrics/quick stats cards: Total Payments, Total Collected, Average Payment Value, Highest Payment, Overdue Invoices Count, Collection Rate %
  - [x] Add filter bar for date range preset, custom date inputs, payment mode, invoice status, amount range, customer name, and invoice number
  - [x] Implement Global Search input
  - [x] Formulate detailed desktop table, tablet compact layout, and card-based mobile layout
  - [x] Embed detailed payment details modal
- [x] Update DTO & Controller
  - [x] Add `invoiceAmount`, `remainingBalance`, `status`, `notes`, `createdAt`, and `createdBy` fields to `InvoicePaymentListDTO.java`
  - [x] Update `/api/payments/list` inside `SalesController.java` to populate all new DTO properties
- [x] Develop JS Logic in `sales-payment.js`
  - [x] Query `/api/payments/list` and `/api/invoices` in parallel
  - [x] Formulate summary/dashboard calculations dynamically
  - [x] Build search, preset/custom date, mode, status, amount, customer, and invoice filtering
  - [x] Construct pagination controls (10/25/50/100 records per page)
  - [x] Program detail modal display on row click
  - [x] Design Excel, CSV, and PDF export functions honoring filters

## Verification
- [x] Compile backend with Maven successfully
