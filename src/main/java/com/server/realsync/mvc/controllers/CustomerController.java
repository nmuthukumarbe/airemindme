package com.server.realsync.mvc.controllers;

import java.util.Optional;
import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import com.server.realsync.entity.Account;
import com.server.realsync.entity.Customer;
import com.server.realsync.services.CustomerService;
import com.server.realsync.util.SecurityUtil;

@RestController
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // ===============================
    // CREATE CUSTOMER API
    // ===============================

    @PostMapping("/api/customers")
    @ResponseBody
    public ResponseEntity<?> createCustomer(@RequestBody Customer customer) {

        Account account = SecurityUtil.getCurrentAccountId();
        customer.setAccountId(account.getId());

        Optional<Customer> existing = customerService.findByMobile(account.getId(), customer.getMobile());

        if (existing.isPresent()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "Customer with this mobile already exists"));
        }

        Customer saved = customerService.save(customer);

        return ResponseEntity.ok(saved);
    }

    // ===============================
    // UPDATE CUSTOMER API
    // ===============================

    @PutMapping("/api/customers/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Integer id,
            @RequestBody Customer customer) {

        Account account = SecurityUtil.getCurrentAccountId();

        Optional<Customer> optionalCustomer = customerService.getById(id);

        if (optionalCustomer.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message",
                            "Customer not found"));
        }

        Customer existing = optionalCustomer.get();

        if (!existing.getAccountId().equals(account.getId())) {
            return ResponseEntity.status(403)
                    .body(Map.of("message",
                            "Unauthorized"));
        }

        Optional<Customer> duplicate = customerService.findByMobile(
                account.getId(),
                customer.getMobile());

        if (duplicate.isPresent()
                && !duplicate.get().getId().equals(id)) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            "Customer with this mobile already exists"));
        }

        existing.setName(customer.getName());
        existing.setMobile(customer.getMobile());
        existing.setEmail(customer.getEmail());
        existing.setDob(customer.getDob());
        existing.setWeddingDate(customer.getWeddingDate());
        existing.setChannel(customer.getChannel());
        existing.setCustomerGroupId(customer.getCustomerGroupId());

        Customer saved = customerService.save(existing);

        return ResponseEntity.ok(saved);
    }

    // ===============================
    // CUSTOMER BULK IMPORT
    // ===============================
    @PostMapping("/api/customers/import")
    @ResponseBody
    public ResponseEntity<?> importCustomers(@RequestBody List<Customer> customers) {
        try {
            Account account = SecurityUtil.getCurrentAccountId();

            int imported = 0;
            int skipped = 0;
            int failed = 0;
            DateTimeFormatter csvFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            for (Customer c : customers) {

                try {

                    // Fix mobile formatting
                    String mobile = c.getMobile().replaceAll("\\s+", "");
                    c.setMobile(mobile);

                    // Fix DOB format
                    if (c.getDob() != null) {
                        try {
                            String dobStr = c.getDob().toString();
                            if (dobStr.contains("-") && dobStr.length() == 10) {
                                // already correct format
                            }
                        } catch (Exception ignored) {
                        }
                    }

                    // Check duplicate
                    Optional<Customer> existing = customerService.findByMobile(account.getId(), mobile);

                    if (existing.isPresent()) {
                        skipped++;
                        continue;
                    }

                    c.setAccountId(account.getId());

                    customerService.save(c);

                    imported++;

                } catch (Exception dbError) {

                    System.err.println("Failed to save customer " + c.getName());
                    dbError.printStackTrace();

                    failed++;
                }
            }

            // Return a nice 200 OK summary to the frontend
            return ResponseEntity.ok(Map.of(
                    "imported", imported,
                    "skipped", skipped,
                    "failed", failed));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/api/customers/search")
    public ResponseEntity<List<Customer>> searchCustomers(
            @RequestParam String query) {

        Account account = SecurityUtil.getCurrentAccountId();

        Page<Customer> page = customerService.searchByAccount(
                account.getId(),
                query,
                Pageable.ofSize(20));

        return ResponseEntity.ok(page.getContent());
    }

    @GetMapping("/api/customers/my-customers")
    public ResponseEntity<?> getMyCustomers() {

        Account account = SecurityUtil.getCurrentAccountId();

        if (account == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        List<Customer> customers = customerService.getByAccountId(account.getId());

        return ResponseEntity.ok(customers);
    }

    @GetMapping("/api/customers/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable Integer id) {

        Account account = SecurityUtil.getCurrentAccountId();

        Optional<Customer> customer = customerService.getById(id);

        if (customer.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // SECURITY CHECK (IMPORTANT)
        if (!customer.get().getAccountId().equals(account.getId())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }

        return ResponseEntity.ok(customer.get());
    }

    @GetMapping("/api/customers/template")
    public ResponseEntity<Resource> downloadTemplate() {

        String csv = "name,phone,email,segment,channel,birthday,anniversary\n" +
                "Rajesh Kumar,+919876543210,rajesh@gmail.com,VIP,1,1995-06-15,2020-01-10\n";

        ByteArrayResource resource = new ByteArrayResource(csv.getBytes(StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=customer_import_template.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(resource);
    }
}