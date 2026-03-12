package com.server.realsync.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 
 */
@Entity
@Table(name = "promotion_entry")
public class PromotionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "triggered_date", nullable = false)
    private LocalDateTime triggeredDate;

    @Column(name = "sent_sms")
    private Boolean sentSms = false;

    @Column(name = "sent_email")
    private Boolean sentEmail = false;

    @Column(name = "sent_whatsapp")
    private Boolean sentWhatsapp = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public PromotionEntry() {}

    // Getters & Setters

    public Long getId() { return id; }

    public Long getPromotionId() { return promotionId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }

    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }

    public LocalDateTime getTriggeredDate() { return triggeredDate; }
    public void setTriggeredDate(LocalDateTime triggeredDate) { this.triggeredDate = triggeredDate; }

    public Boolean getSentSms() { return sentSms; }
    public void setSentSms(Boolean sentSms) { this.sentSms = sentSms; }

    public Boolean getSentEmail() { return sentEmail; }
    public void setSentEmail(Boolean sentEmail) { this.sentEmail = sentEmail; }

    public Boolean getSentWhatsapp() { return sentWhatsapp; }
    public void setSentWhatsapp(Boolean sentWhatsapp) { this.sentWhatsapp = sentWhatsapp; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}