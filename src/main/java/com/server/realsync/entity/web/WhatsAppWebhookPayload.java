/**
 * 
 */
package com.server.realsync.entity.web;

import java.util.Map;

/**
 * 
 */
//WhatsAppWebhookPayload.java
public class WhatsAppWebhookPayload {
	// Define fields based on actual WhatsApp webhook structure (if needed)
	// You can use Map<String, Object> for flexible structure if unknown
	private Map<String, Object> payload;

	public Map<String, Object> getPayload() {
		return payload;
	}

	public void setPayload(Map<String, Object> payload) {
		this.payload = payload;
	}

	// Getter and Setter
}
