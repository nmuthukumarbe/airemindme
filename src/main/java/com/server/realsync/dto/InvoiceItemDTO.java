package com.server.realsync.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class InvoiceItemDTO {

    private Long id;

    @NotBlank
    private String itemType;

    private Long itemRefId;

    @NotBlank
    private String itemName;

    @NotNull
    @Min(1)
    private Integer qty;

    @NotNull
    private BigDecimal rate;

    private BigDecimal gst;

    private BigDecimal taxAmount;

    private BigDecimal lineTotal;

    public InvoiceItemDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }
    public Long getItemRefId() { return itemRefId; }
    public void setItemRefId(Long itemRefId) { this.itemRefId = itemRefId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public Integer getQty() { return qty; }
    public void setQty(Integer qty) { this.qty = qty; }
    public BigDecimal getRate() { return rate; }
    public void setRate(BigDecimal rate) { this.rate = rate; }
    public BigDecimal getGst() { return gst; }
    public void setGst(BigDecimal gst) { this.gst = gst; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }
}
