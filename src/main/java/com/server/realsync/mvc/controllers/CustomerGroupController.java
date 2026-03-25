package com.server.realsync.mvc.controllers;

import com.server.realsync.entity.Account;
import com.server.realsync.entity.CustomerGroup;
import com.server.realsync.services.CustomerGroupService;
import com.server.realsync.util.SecurityUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-groups")
public class CustomerGroupController {

    private final CustomerGroupService groupService;

    public CustomerGroupController(CustomerGroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * GET all groups for the currently logged-in account.
     * Note: We no longer need accountId in the URL because we find it via
     * SecurityUtil.
     */
    @GetMapping("/my-groups")
    public ResponseEntity<List<CustomerGroup>> getMyGroups() {
        Account account = SecurityUtil.getCurrentAccountId();
        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(groupService.getByAccountId(account.getId()));
    }

    /**
     * POST to save a group.
     * The accountId is set automatically from the session.
     */
    @PostMapping("/save")
    public ResponseEntity<?> saveGroup(@RequestBody CustomerGroup group) {
        // 1. Get the logged-in account from the backend session
        Account account = SecurityUtil.getCurrentAccountId();

        if (account == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User session not found.");
        }

        // 2. Force the accountId into the group entity
        group.setAccountId(account.getId());

        // 3. Logic to check if the name already exists for THIS specific account
        // This prevents a 500 Internal Server Error from the SQL unique constraint
        List<CustomerGroup> existingGroups = groupService.getByAccountId(account.getId());
        boolean exists = existingGroups.stream()
                .anyMatch(g -> g.getName().equalsIgnoreCase(group.getName()));

        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("A group with this name already exists.");
        }

        // 4. Save and return
        return ResponseEntity.ok(groupService.save(group));
    }

    // DELETE a group
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGroup(@PathVariable Integer id) {
        // Optional: Add a check here to ensure the group being deleted
        // actually belongs to the current accountId for better security.
        groupService.delete(id);
        return ResponseEntity.ok().build();
    }
}