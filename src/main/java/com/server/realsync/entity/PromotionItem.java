package com.server.realsync.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "promotion_item")
public class PromotionItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "promotion_id", nullable = false)
    private Long promotionId;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType; // "plan" or "product"

    public PromotionItem() {}

    public PromotionItem(Long promotionId, Integer itemId, String itemType) {
        this.promotionId = promotionId;
        this.itemId = itemId;
        this.itemType = itemType;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPromotionId() { return promotionId; }
    public void setPromotionId(Long promotionId) { this.promotionId = promotionId; }

    public Integer getItemId() { return itemId; }
    public void setItemId(Integer itemId) { this.itemId = itemId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
}
