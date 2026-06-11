package com.server.realsync.dto;

public class UpcomingReminderDTO {
    private Integer id;
    private String title;
    private String customerName;
    private String channel;
    private String reminderDate;
    private String reminderTime;

    public UpcomingReminderDTO() {}

    public UpcomingReminderDTO(Integer id, String title, String customerName, String channel, String reminderDate, String reminderTime) {
        this.id = id;
        this.title = title;
        this.customerName = customerName;
        this.channel = channel;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }
}
