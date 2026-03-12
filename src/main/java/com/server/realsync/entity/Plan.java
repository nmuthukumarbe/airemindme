/**
 * 
 */
package com.server.realsync.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * 
 */
@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @Column(name = "charge_per_ai_photo")
    private Double chargePerAiPhoto;
    
    @Column(name = "charge_per_non_ai_photo")
    private Double chargePerNonAiPhoto;
    
    @Column(name = "charge_per_ai")
    private Double chargePerAI;
    
    @Column(name = "charge_per_non_ai")
    private Double chargePerNonAI;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
   
    @PreUpdate
    protected void onUpdate() {
        updatedDate = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }
    
    @PrePersist
    protected void onCreate() {
        updatedDate = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
    }

	public Double getChargePerAiPhoto() {
		return chargePerAiPhoto;
	}

	public void setChargePerAiPhoto(Double chargePerAiPhoto) {
		this.chargePerAiPhoto = chargePerAiPhoto;
	}

	public Double getChargePerNonAiPhoto() {
		return chargePerNonAiPhoto;
	}

	public void setChargePerNonAiPhoto(Double chargePerNonAiPhoto) {
		this.chargePerNonAiPhoto = chargePerNonAiPhoto;
	}

	public Double getChargePerAI() {
		return chargePerAI;
	}

	public void setChargePerAI(Double chargePerAI) {
		this.chargePerAI = chargePerAI;
	}

	public Double getChargePerNonAI() {
		return chargePerNonAI;
	}

	public void setChargePerNonAI(Double chargePerNonAI) {
		this.chargePerNonAI = chargePerNonAI;
	}
}