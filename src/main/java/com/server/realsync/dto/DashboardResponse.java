/**
 * 
 */
package com.server.realsync.dto;

import java.math.BigDecimal;

/**
 * 
 */
public class DashboardResponse {
    private BigDecimal pendingAmount;
    private BigDecimal receivedAmount;
    private long total;
	public BigDecimal getPendingAmount() {
		return pendingAmount;
	}
	public void setPendingAmount(BigDecimal pendingAmount) {
		this.pendingAmount = pendingAmount;
	}
	public BigDecimal getReceivedAmount() {
		return receivedAmount;
	}
	public void setReceivedAmount(BigDecimal receivedAmount) {
		this.receivedAmount = receivedAmount;
	}
	public long getTotal() {
		return total;
	}
	public void setTotal(long total) {
		this.total = total;
	}
	
}

