package com.server.realsync.mvc.controllers;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.server.realsync.util.SecurityUtil;

import com.server.realsync.entity.Account;
import com.server.realsync.entity.Invoice;
import com.server.realsync.entity.InvoicePayment;
import com.server.realsync.services.AccountService;
import com.server.realsync.services.InvoicePaymentService;
import com.server.realsync.services.InvoiceService;

@Controller
public class SalesController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private InvoicePaymentService paymentService;

    @GetMapping("/sales.html")
    public String getSalesPage(Model model) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        model.addAttribute("account", account);

        return "remindmeui/sales";
    }

    @GetMapping("/invoice-create.html")
    public String getInvoiceCreate(Model model) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        model.addAttribute("account", account);
        model.addAttribute("activePage", "sales");

        return "remindmeui/invoice-create";
    }

    @GetMapping("/invoice-detail.html")
    public String getInvoiceDetail(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) Boolean preview,
            Model model) {

        Account loggedIn = SecurityUtil.getCurrentAccountId();
        Account account = accountService.getById(loggedIn.getId());

        model.addAttribute("account", account);
        model.addAttribute("activePage", "sales");

        return "remindmeui/invoice-detail";
    }

    @PostMapping("/api/invoices/{id}/payments")
    public ResponseEntity<?> recordPayment(
            @PathVariable Integer id,
            @RequestBody InvoicePayment payment) {

        try {

            Account account = SecurityUtil.getCurrentAccountId();

            Optional<Invoice> invoice = invoiceService.findEntityById(Long.valueOf(id));

            if (invoice.isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Invoice not found"));
            }

            if (payment.getAmount() == null ||
                    payment.getAmount() <= 0) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Payment amount must be greater than 0"));
            }

            if (payment.getPaymentMode() == null ||
                    payment.getPaymentMode().isBlank()) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Payment mode is required"));
            }

            if (payment.getPaymentDate() == null) {

                return ResponseEntity
                        .badRequest()
                        .body(Map.of(
                                "success", false,
                                "message", "Payment date is required"));
            }

            payment.setAccountId(account.getId());
            payment.setInvoiceId(id);

            InvoicePayment saved = paymentService.save(payment);

            return ResponseEntity.ok(
                    Map.of(
                            "success", true,
                            "message", "Payment recorded successfully",
                            "paymentId", saved.getId()));

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(Map.of(
                            "success", false,
                            "message", e.getMessage()));
        }
    }

    @GetMapping("/invoices/{id}/payments")
    public ResponseEntity<?> getPayments(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
                paymentService.getByInvoice(id));
    }
}