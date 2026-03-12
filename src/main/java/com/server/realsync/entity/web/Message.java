/**
 * 
 */
package com.server.realsync.entity.web;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

/**
 * 
 */
//Message.java
public class Message {
	private String whatsapp_business_phone_number_id;
	private String whatsapp_message_id;
	private String replied_to_whatsapp_message_id;
	private boolean is_new_message;
	private String body;
	private String status;
	@JsonDeserialize(using = MediaDeserializer.class)
	private Media media;

	public String getWhatsapp_business_phone_number_id() {
		return whatsapp_business_phone_number_id;
	}

	public void setWhatsapp_business_phone_number_id(String whatsapp_business_phone_number_id) {
		this.whatsapp_business_phone_number_id = whatsapp_business_phone_number_id;
	}

	public String getWhatsapp_message_id() {
		return whatsapp_message_id;
	}

	public void setWhatsapp_message_id(String whatsapp_message_id) {
		this.whatsapp_message_id = whatsapp_message_id;
	}

	public String getReplied_to_whatsapp_message_id() {
		return replied_to_whatsapp_message_id;
	}

	public void setReplied_to_whatsapp_message_id(String replied_to_whatsapp_message_id) {
		this.replied_to_whatsapp_message_id = replied_to_whatsapp_message_id;
	}

	public boolean isIs_new_message() {
		return is_new_message;
	}

	public void setIs_new_message(boolean is_new_message) {
		this.is_new_message = is_new_message;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Media getMedia() {
		return media;
	}

	public void setMedia(Media media) {
		this.media = media;
	}

	// Getters and Setters
}
