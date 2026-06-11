package com.server.realsync.dto;

public class ImportValidationResultDto {
    private String name;
    private String mobile;
    private String email;
    private String customerGroup;
    private String status; // "Ready", "Warning", "Failed"
    private String reason; // Descriptive validation message
    private CustomerImportDto rawData;

    public ImportValidationResultDto() {}

    public ImportValidationResultDto(String name, String mobile, String email, String customerGroup, String status, String reason, CustomerImportDto rawData) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.customerGroup = customerGroup;
        this.status = status;
        this.reason = reason;
        this.rawData = rawData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerGroup() {
        return customerGroup;
    }

    public void setCustomerGroup(String customerGroup) {
        this.customerGroup = customerGroup;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public CustomerImportDto getRawData() {
        return rawData;
    }

    public void setRawData(CustomerImportDto rawData) {
        this.rawData = rawData;
    }
}
