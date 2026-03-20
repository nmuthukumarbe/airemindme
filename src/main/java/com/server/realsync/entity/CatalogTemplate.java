package com.server.realsync.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "catalog_template")
public class CatalogTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_id", nullable = false)
    private Integer accountId;

    @Column(nullable = false)
    private String title;

    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * Stored as a comma-separated string, e.g. "WhatsApp,SMS,Email"
     * Use getChannelList() / setChannelList() for Java usage.
     */
    @Column(name = "channels")
    private String channels;

    @Column(nullable = false)
    private String status = "active";

    @Column(name = "created_at")
    private LocalDate createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null)
            createdAt = LocalDate.now();
        if (status == null)
            status = "active";
    }

    // ── Helpers for channels list ──────────────────────────────────────

    @Transient
    public List<String> getChannelList() {
        if (channels == null || channels.isBlank())
            return List.of();
        return Arrays.stream(channels.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    @Transient
    public void setChannelList(List<String> list) {
        this.channels = (list == null || list.isEmpty())
                ? ""
                : String.join(",", list);
    }

    // ── Getters & Setters ──────────────────────────────────────────────

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate d) {
        this.createdAt = d;
    }
}