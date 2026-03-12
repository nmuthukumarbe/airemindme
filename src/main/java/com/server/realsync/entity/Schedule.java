/**
 * 
 */
package com.server.realsync.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "schedule")
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(name = "customer_id")
    private Integer customerId;

    @Column(name = "business_plan_id")
    private Long businessPlanId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType type;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String remarks;

    private BigDecimal amount;

    @Column(name = "before_due_date")
    private Integer beforeDueDate;

    private Boolean sms = false;
    private Boolean email = false;
    private Boolean whatsapp = false;

    @Column(name = "whatsapp_content", length = 2000)
    private String whatsappContent;

    @Column(name = "start_datetime")
    private LocalDateTime startDatetime;

    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_every")
    private RepeatEvery repeatEvery = RepeatEvery.NONE;

    @Column(name = "repeat_count")
    private Integer repeatCount = 1;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @PrePersist
    protected void onCreate() {
        createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now();
    }

}