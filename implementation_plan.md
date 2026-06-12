# Implementation Plan - Invoice Detail Flow and Payment Validation

We need to fix the invoice detail page actions, preview behavior, edit redirects, and payment validation/error handling.

## User Review Required

> [!IMPORTANT]
> The existing edit flow uses both query parameters (`id`, `edit`, `previewEdit`). We will ensure that when editing a saved draft, it does not append `preview=true` or similar preview params when returning, and goes to the standard saved detail view.
> We will ensure all backend errors are correctly formatted as JSON so the frontend can retrieve the custom error messages.

## Proposed Changes

---

### Frontend

#### [MODIFY] [invoice-detail.html](file:///d:/files/FREELANCING%20PROJECTS/Remindme/airemindme/src/main/resources/templates/remindmeui/invoice-detail.html)

- Update elements dynamically to identify elements. We will add an ID to the Payment Summary card: `id="paymentSummaryCard"`.
- Revise the UI application script to handle preview versus saved statuses dynamically.
- In preview mode:
  - Show preview controls (Save Draft, Save Invoice, Preview Banner).
  - Hide all of: Record Payment buttons, Activity Timeline, Payment Summary/Tracking, Print button, and Customer actions (`savedInvoiceActionsRow`).
- In saved mode:
  - Never show preview controls (Save Draft, Save Invoice, Preview Banner).
  - Apply the following rules based on the invoice status:
    - **DRAFT**: Show Edit, Show Print, Show Activity Timeline, Hide Record Payment (hide `headerRecordPaymentBtn` & `recordPaymentBtn`).
    - **SENT**: Show Edit, Show Print, Show Record Payment (show `headerRecordPaymentBtn` & `recordPaymentBtn`).
    - **PARTIALLY_PAID**: Show Edit, Show Print, Show Record Payment.
    - **PAID**: Show Print, Disable payment actions (disable `headerRecordPaymentBtn` & `recordPaymentBtn`), Hide/Disable Edit.
    - **CANCELLED**: Show Print, Hide payment actions (hide `headerRecordPaymentBtn` & `recordPaymentBtn`), Disable edit (disable/hide Edit buttons).
- Change the redirect after `Save Draft` is clicked in preview mode: redirect to the normal invoice detail page (e.g. `/invoice-detail.html?id=${savedInvoice.id}`) instead of the create page.
- Update `recordPayment()` to execute client-side validations and display specific toast messages:
  1. Empty Amount: `"Please enter payment amount."`
  2. Amount <= 0: `"Payment amount must be greater than zero."`
  3. Exceeds Balance: `"Payment amount cannot exceed remaining balance of ₹352.49."` (dynamic balance)
  4. Missing Date: `"Please select payment date."`
  5. Future Date: `"Payment date cannot be in the future."`
  6. Backend failure: Extract actual backend message and toast it.
  7. Network/API failure: `"Unable to connect. Please try again."`

---

### Backend

#### [MODIFY] [SalesController.java](file:///d:/files/FREELANCING%20PROJECTS/Remindme/airemindme/src/main/java/com/server/realsync/mvc/controllers/SalesController.java)

- Update `/api/invoices/{id}/payments` POST handler to perform robust validations:
  - If payment amount is null: return 400 with message `"Please enter payment amount."`
  - If payment amount <= 0: return 400 with message `"Payment amount must be greater than zero."`
  - If payment date is null: return 400 with message `"Please select payment date."`
  - If payment date is in the future: return 400 with message `"Payment date cannot be in the future."`
  - If payment exceeds remaining balance: return 400 with message `"Payment exceeds remaining invoice balance."`

## Verification Plan

### Automated Tests
- We will verify that the compilation succeeds after backend changes.

### Manual Verification
- Check Preview Mode: Open invoice detail with `preview=true` in query param. Ensure Preview banner and Save Draft / Save Invoice buttons show, and others are hidden.
- Check Save Draft: Click Save Draft from preview. Verify redirect goes to the normal detail page, showing saved actions (Edit, Print, Activity Timeline, but NOT Save Draft/Save Invoice buttons/banner or Record Payment).
- Check Status-specific Actions (DRAFT, SENT, PAID, CANCELLED).
- Verify all validation scenarios for payment amounts and dates.
