/**
 * 
 */
package com.server.realsync.entity.web;

import java.util.Map;

/**
 * 
 */
//WebhookRequest.java
public class WebhookRequest {
	private Contact contact;
	private Message message;
	private Map<String, Object> whatsapp_webhook_payload;

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}

	public Map<String, Object> getWhatsapp_webhook_payload() {
		return whatsapp_webhook_payload;
	}

	public void setWhatsapp_webhook_payload(Map<String, Object> whatsapp_webhook_payload) {
		this.whatsapp_webhook_payload = whatsapp_webhook_payload;
	}

	// Getters and Setters
}
