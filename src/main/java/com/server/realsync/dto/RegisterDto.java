package com.server.realsync.dto;

public class RegisterDto {

	private String name;
	private String email;
	private String mobile;
	private String password;
	private String refAccId;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRefAccId() {
		return refAccId;
	}

	public void setRefAccId(String refAccId) {
		this.refAccId = refAccId;
	}

}
