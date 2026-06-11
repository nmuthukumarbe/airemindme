package com.server.realsync.dto;

import java.time.LocalDateTime;

public class RecentActivityDTO {
    private String type;
    private String title;
    private String customerName;
    private String status;
    private LocalDateTime createdAt;

    public RecentActivityDTO() {}

    public RecentActivityDTO(String type, String title, String customerName, String status, LocalDateTime createdAt) {
        this.type = type;
        this.title = title;
        this.customerName = customerName;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
