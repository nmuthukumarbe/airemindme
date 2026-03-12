/**
 * 
 */
package com.server.realsync.dto;

/**
 * 
 */
public class GuestPageDTO {

    private String coverImgUrl;
    private Integer coverLogoSize;
    private Boolean coverIsLogoRequired;
    private Boolean coverIsSocialRequired;
    private Boolean coverIsNameRequired;
    private Boolean coverIsEnquiryRequired;
    private String footerNote;
    private Integer footerLogoSize;
    private Boolean footerIsLogoRequired;
    private Boolean footerIsSocialRequired;
    private Boolean footerIsNameRequired;
    private Boolean footerIsEnquiryRequired;

    // Constructors, Getters, and Setters

    public GuestPageDTO(String coverImgUrl, Integer coverLogoSize, Boolean coverIsLogoRequired, Boolean coverIsSocialRequired, Boolean coverIsNameRequired, Boolean coverIsEnquiryRequired, String footerNote, Integer footerLogoSize, Boolean footerIsLogoRequired, Boolean footerIsSocialRequired, Boolean footerIsNameRequired, Boolean footerIsEnquiryRequired) {
        this.coverImgUrl = coverImgUrl;
        this.coverLogoSize = coverLogoSize;
        this.coverIsLogoRequired = coverIsLogoRequired;
        this.coverIsSocialRequired = coverIsSocialRequired;
        this.coverIsNameRequired = coverIsNameRequired;
        this.coverIsEnquiryRequired = coverIsEnquiryRequired;
        this.footerNote = footerNote;
        this.footerLogoSize = footerLogoSize;
        this.footerIsLogoRequired = footerIsLogoRequired;
        this.footerIsSocialRequired = footerIsSocialRequired;
        this.footerIsNameRequired = footerIsNameRequired;
        this.footerIsEnquiryRequired = footerIsEnquiryRequired;
    }

	public String getCoverImgUrl() {
		return coverImgUrl;
	}

	public void setCoverImgUrl(String coverImgUrl) {
		this.coverImgUrl = coverImgUrl;
	}

	public Integer getCoverLogoSize() {
		return coverLogoSize;
	}

	public void setCoverLogoSize(Integer coverLogoSize) {
		this.coverLogoSize = coverLogoSize;
	}

	public Boolean getCoverIsLogoRequired() {
		return coverIsLogoRequired;
	}

	public void setCoverIsLogoRequired(Boolean coverIsLogoRequired) {
		this.coverIsLogoRequired = coverIsLogoRequired;
	}

	public Boolean getCoverIsSocialRequired() {
		return coverIsSocialRequired;
	}

	public void setCoverIsSocialRequired(Boolean coverIsSocialRequired) {
		this.coverIsSocialRequired = coverIsSocialRequired;
	}

	public Boolean getCoverIsNameRequired() {
		return coverIsNameRequired;
	}

	public void setCoverIsNameRequired(Boolean coverIsNameRequired) {
		this.coverIsNameRequired = coverIsNameRequired;
	}

	public Boolean getCoverIsEnquiryRequired() {
		return coverIsEnquiryRequired;
	}

	public void setCoverIsEnquiryRequired(Boolean coverIsEnquiryRequired) {
		this.coverIsEnquiryRequired = coverIsEnquiryRequired;
	}

	public String getFooterNote() {
		return footerNote;
	}

	public void setFooterNote(String footerNote) {
		this.footerNote = footerNote;
	}

	public Integer getFooterLogoSize() {
		return footerLogoSize;
	}

	public void setFooterLogoSize(Integer footerLogoSize) {
		this.footerLogoSize = footerLogoSize;
	}

	public Boolean getFooterIsLogoRequired() {
		return footerIsLogoRequired;
	}

	public void setFooterIsLogoRequired(Boolean footerIsLogoRequired) {
		this.footerIsLogoRequired = footerIsLogoRequired;
	}

	public Boolean getFooterIsSocialRequired() {
		return footerIsSocialRequired;
	}

	public void setFooterIsSocialRequired(Boolean footerIsSocialRequired) {
		this.footerIsSocialRequired = footerIsSocialRequired;
	}

	public Boolean getFooterIsNameRequired() {
		return footerIsNameRequired;
	}

	public void setFooterIsNameRequired(Boolean footerIsNameRequired) {
		this.footerIsNameRequired = footerIsNameRequired;
	}

	public Boolean getFooterIsEnquiryRequired() {
		return footerIsEnquiryRequired;
	}

	public void setFooterIsEnquiryRequired(Boolean footerIsEnquiryRequired) {
		this.footerIsEnquiryRequired = footerIsEnquiryRequired;
	}

    // Add the corresponding getters and setters here
    
}