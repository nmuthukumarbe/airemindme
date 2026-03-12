/**
 * 
 */
package com.server.realsync.dto;

/**
 * 
 */
public class PasswordResetDto {

    private Integer accountId;
    private Long userId;
    private String currentPassword;
    private String currentUserPassword;
    private String newPassword;
    
	public Integer getAccountId() {
		return accountId;
	}
	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}
	public String getCurrentPassword() {
		return currentPassword;
	}
	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	public String getCurrentUserPassword() {
		return currentUserPassword;
	}
	public void setCurrentUserPassword(String currentUserPassword) {
		this.currentUserPassword = currentUserPassword;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
    

}