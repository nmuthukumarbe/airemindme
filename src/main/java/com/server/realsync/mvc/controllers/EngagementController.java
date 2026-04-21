package com.server.realsync.mvc.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;

import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.server.realsync.util.SecurityUtil;
import com.server.realsync.entity.Account;
import com.server.realsync.entity.Reminder;
import com.server.realsync.entity.Greeting;
import com.server.realsync.services.ReminderService;
import com.server.realsync.services.GreetingService; // Assuming you have this service

@RestController
@RequestMapping("/api/engagements")
@CrossOrigin(origins = "*")
public class EngagementController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private GreetingService greetingService;

    // 1. REMINDER APIS

    @GetMapping("/reminders/account/{accountId}")
    public List<Reminder> getAllReminders(@PathVariable Integer accountId) {
        return reminderService.getByAccountId(accountId);
    }

    @GetMapping("/reminders/{id}")
    public ResponseEntity<Reminder> getReminderById(@PathVariable Integer id, @RequestParam Integer accountId) {
        return reminderService.getById(id, accountId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/reminders")
    public Reminder createReminder(@RequestBody Reminder reminder) {
        if (reminder.getAccountId() == null || reminder.getCustomerId() == null) {
            throw new RuntimeException("AccountId and CustomerId are required");
        }
        return reminderService.save(reminder);
    }

    @PutMapping("/reminders/{id}")
    public Reminder updateReminder(@PathVariable Integer id, @RequestParam Integer accountId,
            @RequestBody Reminder reminder) {
        Optional<Reminder> existing = reminderService.getById(id, accountId);
        if (existing.isEmpty()) {
            throw new RuntimeException("Reminder not found or unauthorized");
        }
        reminder.setId(id);
        reminder.setAccountId(accountId);
        return reminderService.save(reminder);
    }

    @DeleteMapping("/reminders/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Integer id) {
        Account account = SecurityUtil.getCurrentAccountId();
        reminderService.delete(id, account.getId());
        return ResponseEntity.ok(Map.of("message", "Reminder deleted successfully"));
    }
    // 2. GREETING APIS (Added for completeness)

    @GetMapping("/greetings/account/{accountId}")
    public List<Greeting> getGreetings(@PathVariable Integer accountId) {
        return greetingService.getByAccountId(accountId);
    }

    @PostMapping("/greetings")
    public ResponseEntity<Greeting> createGreeting(@RequestBody Greeting greeting) {
        // Basic validation
        if (greeting.getAccountId() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Set default status if not provided by frontend
        if (greeting.getStatus() == null) {
            greeting.setStatus("Scheduled");
        }

        Greeting savedGreeting = greetingService.save(greeting);
        return ResponseEntity.ok(savedGreeting);
    }

    @DeleteMapping("/greetings/{id}")
    public ResponseEntity<?> deleteGreeting(@PathVariable Integer id, @RequestParam Integer accountId) {
        try {
            greetingService.delete(id, accountId);
            return ResponseEntity.ok(Map.of("message", "Greeting deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Could not delete greeting"));
        }
    }

    // 3. STATS APIS (Fixed Path Inconsistency)

    // GET /api/engagements/count/scheduled/1
    @GetMapping("/count/scheduled/{accountId}")
    public long countScheduled(@PathVariable Integer accountId) {
        return reminderService.countScheduledByAccountId(accountId);
    }

    // GET /api/engagements/count/sent-today/1
    @GetMapping("/count/sent-today/{accountId}")
    public long countSentToday(@PathVariable Integer accountId) {
        return reminderService.countSentToday(accountId);
    }

    // GET /api/engagements/stats/1
    @GetMapping("/stats/{accountId}")
    public ResponseEntity<?> getFullStats(@PathVariable Integer accountId) {
        try {
            // Calculate each stat safely
            long totalReminders = reminderService.getByAccountId(accountId).size();
            long totalGreetings = greetingService.getByAccountId(accountId).size();

            // Ensure these service methods return 0 instead of throwing errors if empty
            long scheduled = reminderService.countScheduledByAccountId(accountId);
            long sentToday = reminderService.countSentToday(accountId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("reminders", totalReminders);
            stats.put("greetings", totalGreetings);
            stats.put("scheduled", scheduled);
            stats.put("sentToday", sentToday);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            // This will tell you EXACTLY what is failing in your IDE console
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error calculating stats: " + e.getMessage());
        }
    }

    @PostMapping("/reminders/{id}/send")
    public ResponseEntity<?> sendNow(@PathVariable Integer id) {
        Account account = SecurityUtil.getCurrentAccountId();

        return ResponseEntity.ok(Map.of("message", "Reminder sent successfully"));
    }

    @PostMapping("/reminders/{id}/reschedule")
    public ResponseEntity<?> reschedule(
            @PathVariable Integer id,
            @RequestParam String date,
            @RequestParam String time) {
        Account account = SecurityUtil.getCurrentAccountId();
        reminderService.reschedule(id, account.getId(), date, time);
        return ResponseEntity.ok(Map.of("message", "Rescheduled successfully"));
    }

    @PostMapping("/reminders/{id}/make-recurring")
    public ResponseEntity<?> makeRecurring(@PathVariable Integer id) {
        Account account = SecurityUtil.getCurrentAccountId();
        reminderService.makeRecurring(id, account.getId());
        return ResponseEntity.ok(Map.of("message", "Converted to recurring"));
    }

    
}