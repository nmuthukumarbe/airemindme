/**
 * 
 */
package com.server.realsync.util;

import com.server.realsync.util.Phonenumber.PhoneNumber;

/**
 * @author apalaniswamy
 *
 */
public class TextMessageVO
{

	private String message;
	
	private PhoneNumber number;
	
	private String dltTemplateId;

	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public PhoneNumber getNumber() {
		return number;
	}

	public void setNumber(PhoneNumber number) {
		this.number = number;
	}

	public String getDltTemplateId() {
		return dltTemplateId;
	}

	public void setDltTemplateId(String dltTemplateId) {
		this.dltTemplateId = dltTemplateId;
	}
	
}
