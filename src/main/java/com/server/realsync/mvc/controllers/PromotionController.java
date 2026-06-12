package com.server.realsync.mvc.controllers;

import com.server.realsync.entity.*;
import com.server.realsync.services.*;
import com.server.realsync.repo.PromotionItemRepository;
import com.server.realsync.repo.PromotionExecutionLogRepository;
import com.server.realsync.dto.PromotionResponseDTO;
import com.server.realsync.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private PromotionEntryService entryService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PromotionItemRepository promotionItemRepository;

    @Autowired
    private PromotionExecutionLogRepository promotionExecutionLogRepository;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CatlogPlanService settingsPlanService;

    @Autowired
    private CatalogProductService catalogProductService;

    /**
     * Creates a promotion, stores selected catalog items, and generates entries for all targeted customers.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody PromotionRequest request) {
        Account account = SecurityUtil.getCurrentAccountId();
        if (account == null) {
            return ResponseEntity.status(401).build();
        }

        // Validation Rules (10)
        if (request.description() == null || request.description().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Description cannot be empty."));
        }
        if (request.itemIds() == null || request.itemIds().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "At least one item must be selected."));
        }
        if (request.groupId() == null && request.customerId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "No recipient selected."));
        }

        LocalDateTime sched = null;
        if (request.scheduledAt() != null && !request.scheduledAt().trim().isEmpty()) {
            try {
                sched = LocalDateTime.parse(request.scheduledAt());
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("message", "Invalid schedule date."));
            }
            if (sched.isBefore(LocalDateTime.now())) {
                return ResponseEntity.badRequest().body(Map.of("message", "Scheduled date cannot be in the past."));
            }
        }

        // Step 1: Create Promotion
        Promotion p = new Promotion();
        p.setAccountId(account.getId());
        p.setCustomerGroupId(request.groupId());
        p.setDescription(request.description());
        p.setImageUrl("");
        p.setType("MANUAL");
        p.setStatus(sched != null ? "SCHEDULED" : "ACTIVE");
        p.setScheduledAt(sched);
        p.setCreatedAt(LocalDateTime.now());

        Promotion saved = promotionService.save(p);

        // Step 2: Save Promotion Items
        if (request.itemIds() != null) {
            System.out.println("[DEBUG] Incoming itemIds count: " + request.itemIds().size());
            for (String compositeId : request.itemIds()) {
                System.out.println("[DEBUG] Processing compositeId: " + compositeId);
                String[] parts = compositeId.split("-");
                if (parts.length >= 2) {
                    String type = parts[0];
                    try {
                        Integer itemId = Integer.parseInt(parts[1]);
                        PromotionItem pi = new PromotionItem(saved.getId(), itemId, type);
                        System.out.println("[DEBUG] Saving PromotionItem: promotionId=" + saved.getId() + ", itemId=" + itemId + ", type=" + type);
                        promotionItemRepository.save(pi);
                    } catch (NumberFormatException e) {
                        System.err.println("[DEBUG] Failed to parse itemId from: " + parts[1] + ". Error: " + e.getMessage());
                    }
                } else {
                    System.err.println("[DEBUG] Invalid compositeId format: " + compositeId + " (Must contain '-')");
                }
            }
        } else {
            System.out.println("[DEBUG] Incoming itemIds array is NULL");
        }

        // Determine recipients and Step 3: Generate Promotion Entries
        List<Customer> customers = new ArrayList<>();
        if (request.customerId() != null) {
            Customer customer = customerService
                    .getById(account.getId(), request.customerId())
                    .orElse(null);
            if (customer != null) {
                customers.add(customer);
            }
        } else if (request.groupId() != null) {
            customers = customerService
                    .getByAccountAndGroup(account.getId(), request.groupId(), Pageable.unpaged())
                    .getContent();
        }

        if (customers.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "No active customers found for the selection."));
        }

        // Save entries and logs
        for (Customer c : customers) {
            PromotionEntry entry = new PromotionEntry();
            entry.setPromotionId(saved.getId());
            entry.setCustomerId(c.getId());
            entry.setTriggeredDate(LocalDateTime.now());
            PromotionEntry savedEntry = entryService.save(entry);

            // Step 4: Generate Execution Logs
            PromotionExecutionLog log = new PromotionExecutionLog();
            log.setPromotionEntryId(savedEntry.getId());
            
            Channel ch = Channel.WHATSAPP;
            if (request.sendVia() != null) {
                if ("sms".equalsIgnoreCase(request.sendVia())) ch = Channel.SMS;
                else if ("email".equalsIgnoreCase(request.sendVia()) || "em".equalsIgnoreCase(request.sendVia())) ch = Channel.EMAIL;
            }
            log.setChannel(ch);
            log.setStatus(ExecutionResult.PENDING);
            log.setResponse("Pending execution");
            promotionExecutionLogRepository.save(log);
        }

        // Return Promotion URL (Promotion ID)
        String link = "http://localhost:8081/promo/" + saved.getId();
        return ResponseEntity.ok(Map.of(
                "id", saved.getId(),
                "link", link
        ));
    }

    @GetMapping
    public List<PromotionResponseDTO> getAll() {
        Account account = SecurityUtil.getCurrentAccountId();
        if (account == null) {
            return new ArrayList<>();
        }
        List<Promotion> promotions = promotionService.getByAccount(account.getId());
        return promotions.stream().map(p -> {
            List<PromotionEntry> entries = entryService.getByPromotion(p.getId());
            long recipientCount = entries.size();
            long totalViews = entries.stream().mapToLong(e -> e.getViewCount() != null ? e.getViewCount() : 0L).sum();
            long totalLikes = entries.stream().mapToLong(e -> e.getLikeCount() != null ? e.getLikeCount() : 0L).sum();
            long totalEnquiries = entries.stream().mapToLong(e -> e.getEnquiryCount() != null ? e.getEnquiryCount() : 0L).sum();
            long firstEntryId = entries.isEmpty() ? 0L : entries.get(0).getId();

            List<PromotionItem> items = promotionItemRepository.findByPromotionId(p.getId());
            List<String> itemNames = items.stream().map(item -> {
                if ("plan".equalsIgnoreCase(item.getItemType())) {
                    return settingsPlanService.getById(item.getItemId())
                            .map(CatalogPlan::getName)
                            .orElse("Plan #" + item.getItemId());
                } else {
                    return catalogProductService.getById(item.getItemId(), account.getId())
                            .map(CatalogProduct::getName)
                            .orElse("Product #" + item.getItemId());
                }
            }).collect(Collectors.toList());

            String name = p.getAiGeneratedTitle() != null && !p.getAiGeneratedTitle().isEmpty() 
                    ? p.getAiGeneratedTitle() 
                    : (p.getDescription().length() > 30 ? p.getDescription().substring(0, 30) + "..." : p.getDescription());

            return new PromotionResponseDTO(
                    p.getId(),
                    name,
                    p.getDescription(),
                    itemNames,
                    recipientCount,
                    p.getStatus() != null ? p.getStatus() : "ACTIVE",
                    totalViews,
                    totalLikes,
                    totalEnquiries,
                    p.getCreatedAt(),
                    p.getScheduledAt(),
                    firstEntryId
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/public/{promotionId}")
    public ResponseEntity<Map<String, Object>> getPublicPromoLanding(@PathVariable Long promotionId, @RequestParam(value = "entry", required = false) Long entryId) {
        Promotion promo = promotionService.getById(promotionId).orElse(null);
        if (promo == null) {
            return ResponseEntity.notFound().build();
        }

        List<PromotionEntry> entries = entryService.getByPromotion(promo.getId());
        long viewCount = entries.stream().mapToLong(e -> e.getViewCount() != null ? e.getViewCount() : 0L).sum();
        long likeCount = entries.stream().mapToLong(e -> e.getLikeCount() != null ? e.getLikeCount() : 0L).sum();
        long enquiryCount = entries.stream().mapToLong(e -> e.getEnquiryCount() != null ? e.getEnquiryCount() : 0L).sum();

        // Find customer name and actual entry ID for tracking
        String customerName = "Customer";
        Long targetEntryId = null;
        if (entryId != null) {
            PromotionEntry entry = entryService.getById(entryId).orElse(null);
            if (entry != null && entry.getPromotionId().equals(promo.getId())) {
                targetEntryId = entry.getId();
                Customer customer = customerService.getById(promo.getAccountId(), entry.getCustomerId()).orElse(null);
                if (customer != null) {
                    customerName = customer.getName();
                }
            }
        }
        
        if (targetEntryId == null && !entries.isEmpty()) {
            targetEntryId = entries.get(0).getId();
            Customer customer = customerService.getById(promo.getAccountId(), entries.get(0).getCustomerId()).orElse(null);
            if (customer != null) {
                customerName = customer.getName();
            }
        }

        List<PromotionItem> promotionItems = promotionItemRepository.findByPromotionId(promo.getId());
        List<Map<String, Object>> normalizedItems = promotionItems.stream().map(item -> {
            Map<String, Object> map = new java.util.HashMap<>();
            if ("plan".equalsIgnoreCase(item.getItemType())) {
                CatalogPlan plan = settingsPlanService.getById(item.getItemId()).orElse(null);
                if (plan != null) {
                    map.put("id", "plan-" + plan.getId());
                    map.put("type", "PLAN");
                    map.put("name", plan.getName());
                    map.put("price", "₹" + (plan.getPrice() != null ? plan.getPrice() : 0));
                    map.put("priceNote", plan.getBillingCycle() != null ? " / " + plan.getBillingCycle() : "");
                    map.put("desc", plan.getDescription() != null ? plan.getDescription() : "");
                    map.put("features", plan.getFeatures() != null ? List.of(plan.getFeatures().split(",")) : new ArrayList<>());
                    map.put("img", plan.getImageUrl() != null && plan.getImageUrl().length() > 5
                            ? "/doc/view?path=" + java.net.URLEncoder.encode(plan.getImageUrl().replaceFirst("^/+", ""), java.nio.charset.StandardCharsets.UTF_8)
                            : "https://images.unsplash.com/photo-1554224155-6726b3ff858f?w=400&q=80");
                }
            } else {
                CatalogProduct prod = catalogProductService.getById(item.getItemId(), promo.getAccountId()).orElse(null);
                if (prod != null) {
                    map.put("id", "product-" + prod.getId());
                    map.put("type", "PRODUCT");
                    map.put("name", prod.getName());
                    map.put("price", "₹" + (prod.getPrice() != null ? prod.getPrice() : 0));
                    map.put("priceNote", "");
                    map.put("code", prod.getSku() != null ? prod.getSku() : "N/A");
                    map.put("desc", prod.getDescription() != null ? prod.getDescription() : "");
                    map.put("features", new ArrayList<>());
                    map.put("img", prod.getImageUrl() != null && prod.getImageUrl().length() > 5
                            ? "/doc/view?path=" + java.net.URLEncoder.encode(prod.getImageUrl().replaceFirst("^/+", ""), java.nio.charset.StandardCharsets.UTF_8)
                            : "https://images.unsplash.com/photo-1512428559087-560fa5ceab42?w=400&q=80");
                }
            }
            return map;
        }).filter(m -> !m.isEmpty()).collect(Collectors.toList());

        Map<String, Object> accountMap = new java.util.HashMap<>();
        if (accountService != null) {
            Account act = accountService.getById(promo.getAccountId());
            if (act != null) {
                accountMap.put("name", act.getBusinessName() != null ? act.getBusinessName() : "Numen");
                accountMap.put("phone", act.getBusinessPhone() != null ? act.getBusinessPhone() : "ERROR: No Phone");
                accountMap.put("email", act.getBusinessEmail() != null ? act.getBusinessEmail() : "ERROR: No Email");
            }
        }

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("entryId", targetEntryId);
        response.put("promotionId", promo.getId());
        response.put("promotionTitle", promo.getAiGeneratedTitle() != null && !promo.getAiGeneratedTitle().isEmpty() ? promo.getAiGeneratedTitle() : "Exclusive Offers");
        response.put("promotionDescription", promo.getDescription());
        response.put("customerName", customerName);
        response.put("items", normalizedItems);
        response.put("account", accountMap);
        response.put("recipientCount", entries.size());
        response.put("viewCount", viewCount);
        response.put("likeCount", likeCount);
        response.put("enquiryCount", enquiryCount);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/public/{entryId}/view")
    @Transactional
    public ResponseEntity<Void> trackView(@PathVariable Long entryId) {
        PromotionEntry entry = entryService.getById(entryId).orElse(null);
        if (entry != null) {
            entry.setViewCount((entry.getViewCount() != null ? entry.getViewCount() : 0) + 1);
            if (entry.getFirstViewedAt() == null) {
                entry.setFirstViewedAt(LocalDateTime.now());
            }
            entry.setLastViewedAt(LocalDateTime.now());
            entryService.save(entry);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/public/{entryId}/like")
    @Transactional
    public ResponseEntity<Void> trackLike(@PathVariable Long entryId) {
        PromotionEntry entry = entryService.getById(entryId).orElse(null);
        if (entry != null) {
            entry.setLikeCount((entry.getLikeCount() != null ? entry.getLikeCount() : 0) + 1);
            entry.setLikedAt(LocalDateTime.now());
            entryService.save(entry);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/public/{entryId}/enquiry")
    @Transactional
    public ResponseEntity<Void> trackEnquiry(@PathVariable Long entryId) {
        PromotionEntry entry = entryService.getById(entryId).orElse(null);
        if (entry != null) {
            entry.setEnquiryCount((entry.getEnquiryCount() != null ? entry.getEnquiryCount() : 0) + 1);
            entry.setEnquiryAt(LocalDateTime.now());
            entryService.save(entry);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/public/{entryId}/whatsapp-click")
    @Transactional
    public ResponseEntity<Void> trackWhatsappClick(@PathVariable Long entryId) {
        PromotionEntry entry = entryService.getById(entryId).orElse(null);
        if (entry != null) {
            entry.setWhatsappClickCount((entry.getWhatsappClickCount() != null ? entry.getWhatsappClickCount() : 0) + 1);
            entry.setWhatsappClickedAt(LocalDateTime.now());
            entryService.save(entry);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/public/{entryId}/phone-click")
    @Transactional
    public ResponseEntity<Void> trackPhoneClick(@PathVariable Long entryId) {
        PromotionEntry entry = entryService.getById(entryId).orElse(null);
        if (entry != null) {
            entry.setPhoneClickCount((entry.getPhoneClickCount() != null ? entry.getPhoneClickCount() : 0) + 1);
            entry.setPhoneClickedAt(LocalDateTime.now());
            entryService.save(entry);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/public/{entryId}/email-click")
    @Transactional
    public ResponseEntity<Void> trackEmailClick(@PathVariable Long entryId) {
        PromotionEntry entry = entryService.getById(entryId).orElse(null);
        if (entry != null) {
            entry.setEmailClickCount((entry.getEmailClickCount() != null ? entry.getEmailClickCount() : 0) + 1);
            entry.setEmailClickedAt(LocalDateTime.now());
            entryService.save(entry);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{promotionId}/analytics")
    public ResponseEntity<Map<String, Object>> getPromotionAnalytics(@PathVariable Long promotionId) {
        List<PromotionEntry> entries = entryService.getByPromotion(promotionId);
        long totalViews = entries.stream().mapToLong(e -> e.getViewCount() != null ? e.getViewCount() : 0L).sum();
        long totalLikes = entries.stream().mapToLong(e -> e.getLikeCount() != null ? e.getLikeCount() : 0L).sum();
        long totalEnquiries = entries.stream().mapToLong(e -> e.getEnquiryCount() != null ? e.getEnquiryCount() : 0L).sum();
        long totalWhatsappClicks = entries.stream().mapToLong(e -> e.getWhatsappClickCount() != null ? e.getWhatsappClickCount() : 0L).sum();
        long totalPhoneClicks = entries.stream().mapToLong(e -> e.getPhoneClickCount() != null ? e.getPhoneClickCount() : 0L).sum();
        long totalEmailClicks = entries.stream().mapToLong(e -> e.getEmailClickCount() != null ? e.getEmailClickCount() : 0L).sum();

        Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("promotionId", promotionId);
        summary.put("recipientCount", entries.size());
        summary.put("totalViews", totalViews);
        summary.put("totalLikes", totalLikes);
        summary.put("totalEnquiries", totalEnquiries);
        summary.put("totalWhatsappClicks", totalWhatsappClicks);
        summary.put("totalPhoneClicks", totalPhoneClicks);
        summary.put("totalEmailClicks", totalEmailClicks);

        return ResponseEntity.ok(summary);
    }
}

/**
 * Data Transfer Object (DTO) to handle the incoming JSON payload safely.
 */
record PromotionRequest(Integer groupId, Integer customerId, String description, List<String> itemIds, String sendVia, String scheduledAt) {
}
