package com.server.realsync.mvc.controllers;

import com.server.realsync.entity.*;
import com.server.realsync.services.*;
import com.server.realsync.repo.PromotionItemRepository;
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
    private CatlogPlanService settingsPlanService;

    @Autowired
    private CatalogProductService catalogProductService;

    /**
     * Creates a promotion, stores selected catalog items, and generates entries for all targeted customers.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> create(@RequestBody PromotionRequest request) {
        Account account = SecurityUtil.getCurrentAccountId();
        if (account == null) {
            return ResponseEntity.status(401).build();
        }

        // 1. Initialize the Promotion entity
        Promotion p = new Promotion();
        p.setAccountId(account.getId());
        p.setCustomerGroupId(request.groupId()); // Can be null for individual sends
        p.setDescription(request.description());
        p.setImageUrl("");
        p.setType("MANUAL");
        p.setStatus("ACTIVE");
        p.setCreatedAt(LocalDateTime.now());

        // 2. Save the parent Promotion first
        Promotion saved = promotionService.save(p);

        // 2b. Save attached items (plans/products)
        if (request.itemIds() != null) {
            for (String compositeId : request.itemIds()) {
                String[] parts = compositeId.split("-");
                if (parts.length >= 2) {
                    String type = parts[0];
                    try {
                        Integer itemId = Integer.parseInt(parts[1]);
                        PromotionItem pi = new PromotionItem(saved.getId(), itemId, type);
                        promotionItemRepository.save(pi);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        // 3. Determine the list of recipients
        List<Customer> customers;
        if (request.customerId() != null) {
            Customer customer = customerService
                    .getById(account.getId(), request.customerId())
                    .orElse(null);

            if (customer == null) {
                return ResponseEntity.badRequest().body(null);
            }
            customers = List.of(customer);
        } else if (request.groupId() != null) {
            customers = customerService
                    .getByAccountAndGroup(account.getId(), request.groupId(), Pageable.unpaged())
                    .getContent();

            if (customers.isEmpty()) {
                return ResponseEntity.badRequest().body(null);
            }
        } else {
            return ResponseEntity.badRequest().build();
        }

        // 4. Create a PromotionEntry for every customer found
        PromotionEntry firstEntry = null;
        for (Customer c : customers) {
            PromotionEntry entry = new PromotionEntry();
            entry.setPromotionId(saved.getId());
            entry.setCustomerId(c.getId());
            entry.setTriggeredDate(LocalDateTime.now());
            PromotionEntry savedEntry = entryService.save(entry);
            if (firstEntry == null) {
                firstEntry = savedEntry;
            }
        }

        Long targetEntryId = firstEntry != null ? firstEntry.getId() : 0L;
        String link = "http://localhost:8081/promo/" + targetEntryId;

        return ResponseEntity.ok(java.util.Map.of(
                "id", saved.getId(),
                "link", link));
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

            return new PromotionResponseDTO(
                    p.getId(),
                    p.getDescription(),
                    itemNames,
                    recipientCount,
                    p.getStatus() != null ? p.getStatus() : "ACTIVE",
                    totalViews,
                    totalEnquiries,
                    p.getCreatedAt(),
                    firstEntryId
            );
        }).collect(Collectors.toList());
    }

    @GetMapping("/public/{entryId}")
    public ResponseEntity<Map<String, Object>> getPublicPromoLanding(@PathVariable Long entryId) {
        PromotionEntry entry = entryService.getById(entryId).orElse(null);
        if (entry == null) {
            return ResponseEntity.notFound().build();
        }

        Promotion promo = promotionService.getById(entry.getPromotionId()).orElse(null);
        if (promo == null) {
            return ResponseEntity.notFound().build();
        }

        Customer customer = customerService.getById(promo.getAccountId(), entry.getCustomerId()).orElse(null);
        String customerName = customer != null ? customer.getName() : "Customer";

        List<PromotionItem> promotionItems = promotionItemRepository.findByPromotionId(promo.getId());
        List<Map<String, Object>> normalizedItems = promotionItems.stream().map(item -> {
            Map<String, Object> map = new java.util.HashMap<>();
            if ("plan".equalsIgnoreCase(item.getItemType())) {
                CatalogPlan plan = settingsPlanService.getById(item.getItemId()).orElse(null);
                if (plan != null) {
                    map.put("id", "plan-" + plan.getId());
                    map.put("type", "plan");
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
                    map.put("type", "product");
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

        Map<String, Object> response = new java.util.HashMap<>();
        response.put("entryId", entry.getId());
        response.put("promotionId", promo.getId());
        response.put("description", promo.getDescription());
        response.put("customerName", customerName);
        response.put("items", normalizedItems);

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
record PromotionRequest(Integer groupId, Integer customerId, String description, List<String> itemIds, String sendVia) {
}
