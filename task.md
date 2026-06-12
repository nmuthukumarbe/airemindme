# Task Checklist

- [x] Modify `invoice-detail.html`
  - [x] Add ID `paymentSummaryCard` to the Payment Summary container
  - [x] Update `applyPreviewModeUI()` to support both preview mode and saved mode rules based on status
  - [x] Update redirects in `saveInvoiceData()` after Save Draft to point to `invoice-detail.html` with query parameters rather than `invoice-create.html`
  - [x] Update edit draft behavior in `editInvoice()` to ensure proper query parameters are used
  - [x] Add client-side validation logic in `recordPayment()` with specific error messages for empty amount, amount <= 0, amount exceeding balance, missing date, future date, network failure, and backend errors
- [x] Modify `SalesController.java`
  - [x] Update `/api/invoices/{id}/payments` POST handler to validate payment fields: empty amount, amount <= 0, missing date, future date, and payment amount exceeding remaining balance
- [x] Verify Changes
  - [x] Compile backend
  - [x] Perform manual testing verification steps
