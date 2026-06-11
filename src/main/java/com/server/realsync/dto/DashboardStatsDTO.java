package com.server.realsync.dto;

public class DashboardStatsDTO {
    private long customers;
    private long scheduledActivities;
    private long promotions;
    private long upcomingReminders;

    public DashboardStatsDTO() {}

    public DashboardStatsDTO(long customers, long scheduledActivities, long promotions, long upcomingReminders) {
        this.customers = customers;
        this.scheduledActivities = scheduledActivities;
        this.promotions = promotions;
        this.upcomingReminders = upcomingReminders;
    }

    public long getCustomers() {
        return customers;
    }

    public void setCustomers(long customers) {
        this.customers = customers;
    }

    public long getScheduledActivities() {
        return scheduledActivities;
    }

    public void setScheduledActivities(long scheduledActivities) {
        this.scheduledActivities = scheduledActivities;
    }

    public long getPromotions() {
        return promotions;
    }

    public void setPromotions(long promotions) {
        this.promotions = promotions;
    }

    public long getUpcomingReminders() {
        return upcomingReminders;
    }

    public void setUpcomingReminders(long upcomingReminders) {
        this.upcomingReminders = upcomingReminders;
    }
}
