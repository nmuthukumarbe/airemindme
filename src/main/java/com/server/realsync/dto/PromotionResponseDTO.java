package com.server.realsync.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PromotionResponseDTO {
    private Long id;
    private String name;
    private String description;
    private List<String> itemNames;
    private Long recipientCount;
    private String status;
    private Long views;
    private Long likes;
    private Long enquiries;
    private LocalDateTime createdAt;
    private LocalDateTime scheduledAt;
    private Long previewEntryId;

    public PromotionResponseDTO() {}

    public PromotionResponseDTO(Long id, String name, String description, List<String> itemNames, Long recipientCount,
                                String status, Long views, Long likes, Long enquiries, LocalDateTime createdAt,
                                LocalDateTime scheduledAt, Long previewEntryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.itemNames = itemNames;
        this.recipientCount = recipientCount;
        this.status = status;
        this.views = views;
        this.likes = likes;
        this.enquiries = enquiries;
        this.createdAt = createdAt;
        this.scheduledAt = scheduledAt;
        this.previewEntryId = previewEntryId;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<String> getItemNames() { return itemNames; }
    public void setItemNames(List<String> itemNames) { this.itemNames = itemNames; }

    public Long getRecipientCount() { return recipientCount; }
    public void setRecipientCount(Long recipientCount) { this.recipientCount = recipientCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getViews() { return views; }
    public void setViews(Long views) { this.views = views; }

    public Long getLikes() { return likes; }
    public void setLikes(Long likes) { this.likes = likes; }

    public Long getEnquiries() { return enquiries; }
    public void setEnquiries(Long enquiries) { this.enquiries = enquiries; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }

    public Long getPreviewEntryId() { return previewEntryId; }
    public void setPreviewEntryId(Long previewEntryId) { this.previewEntryId = previewEntryId; }
}
