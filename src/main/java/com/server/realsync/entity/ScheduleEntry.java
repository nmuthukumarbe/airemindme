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

/**
 * 
 */


@Entity
@Table(name = "schedule_entry")
public class ScheduleEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "schedule_id", nullable = false)
    private Long scheduleId;

    @Column(name = "occurrence_date", nullable = false)
    private LocalDateTime occurrenceDate;

    private BigDecimal amount;

    @Column(length = 1000)
    private String remarks;

    @Enumerated(EnumType.STRING)
    private ScheduleEntryStatus status = ScheduleEntryStatus.PENDING;

    @Column(name = "sent_sms")
    private Boolean sentSms = false;

    @Column(name = "sent_email")
    private Boolean sentEmail = false;

    @Column(name = "sent_whatsapp")
    private Boolean sentWhatsapp = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "execution_status")
    private ExecutionStatus executionStatus = ExecutionStatus.PENDING;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getScheduleId() {
		return scheduleId;
	}

	public void setScheduleId(Long scheduleId) {
		this.scheduleId = scheduleId;
	}

	public LocalDateTime getOccurrenceDate() {
		return occurrenceDate;
	}

	public void setOccurrenceDate(LocalDateTime occurrenceDate) {
		this.occurrenceDate = occurrenceDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public ScheduleEntryStatus getStatus() {
		return status;
	}

	public void setStatus(ScheduleEntryStatus status) {
		this.status = status;
	}

	public Boolean getSentSms() {
		return sentSms;
	}

	public void setSentSms(Boolean sentSms) {
		this.sentSms = sentSms;
	}

	public Boolean getSentEmail() {
		return sentEmail;
	}

	public void setSentEmail(Boolean sentEmail) {
		this.sentEmail = sentEmail;
	}

	public Boolean getSentWhatsapp() {
		return sentWhatsapp;
	}

	public void setSentWhatsapp(Boolean sentWhatsapp) {
		this.sentWhatsapp = sentWhatsapp;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public ExecutionStatus getExecutionStatus() {
		return executionStatus;
	}

	public void setExecutionStatus(ExecutionStatus executionStatus) {
		this.executionStatus = executionStatus;
	}

}