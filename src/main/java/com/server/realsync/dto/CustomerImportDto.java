package com.server.realsync.dto;

public class CustomerImportDto {
    private String name;
    private String mobile;
    private String email;
    private String customerGroup;
    private String city;
    private String address;
    private String dob;
    private String weddingDate;
    private String gstNo;
    private String whatsAppOptIn;

    public CustomerImportDto() {}

    public CustomerImportDto(String name, String mobile, String email, String customerGroup, String city, String address, String dob, String weddingDate, String gstNo, String whatsAppOptIn) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.customerGroup = customerGroup;
        this.city = city;
        this.address = address;
        this.dob = dob;
        this.weddingDate = weddingDate;
        this.gstNo = gstNo;
        this.whatsAppOptIn = whatsAppOptIn;
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

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getWeddingDate() {
        return weddingDate;
    }

    public void setWeddingDate(String weddingDate) {
        this.weddingDate = weddingDate;
    }

    public String getGstNo() {
        return gstNo;
    }

    public void setGstNo(String gstNo) {
        this.gstNo = gstNo;
    }

    public String getWhatsAppOptIn() {
        return whatsAppOptIn;
    }

    public void setWhatsAppOptIn(String whatsAppOptIn) {
        this.whatsAppOptIn = whatsAppOptIn;
    }
}
