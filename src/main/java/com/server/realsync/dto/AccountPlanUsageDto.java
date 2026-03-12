/**
 * 
 */
package com.server.realsync.dto;

import java.time.LocalDate;

/**
 * 
 */
public class AccountPlanUsageDto {

    private Integer photosUsed;
    private Integer eventsUsed;
    private Integer collagesUsed;
    private Integer photoBalance;
    private Integer eventBalance;
    private Integer collageBalance;
    private LocalDate startDate;
    private LocalDate endDate;

    // Getters and Setters

    public Integer getPhotosUsed() {
        return photosUsed;
    }

    public void setPhotosUsed(Integer photosUsed) {
        this.photosUsed = photosUsed;
    }

    public Integer getEventsUsed() {
        return eventsUsed;
    }

    public void setEventsUsed(Integer eventsUsed) {
        this.eventsUsed = eventsUsed;
    }

    public Integer getPhotoBalance() {
        return photoBalance;
    }

    public void setPhotoBalance(Integer photoBalance) {
        this.photoBalance = photoBalance;
    }

    public Integer getEventBalance() {
        return eventBalance;
    }

    public void setEventBalance(Integer eventBalance) {
        this.eventBalance = eventBalance;
    }

	public Integer getCollagesUsed() {
		return collagesUsed;
	}

	public void setCollagesUsed(Integer collagesUsed) {
		this.collagesUsed = collagesUsed;
	}

	public Integer getCollageBalance() {
		return collageBalance;
	}

	public void setCollageBalance(Integer collageBalance) {
		this.collageBalance = collageBalance;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}