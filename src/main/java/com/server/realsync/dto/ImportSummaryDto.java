package com.server.realsync.dto;

import java.util.List;

public class ImportSummaryDto {
    private int totalRows;
    private int readyCount;
    private int warningCount;
    private int duplicateDbCount;
    private int duplicateCsvCount;
    private int invalidEmailCount;
    private int invalidMobileCount;
    private int invalidGstCount;
    private int invalidDobCount;
    private int invalidAnniversaryCount;
    private List<ImportValidationResultDto> results;

    public ImportSummaryDto() {}

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public int getReadyCount() {
        return readyCount;
    }

    public void setReadyCount(int readyCount) {
        this.readyCount = readyCount;
    }

    public int getWarningCount() {
        return warningCount;
    }

    public void setWarningCount(int warningCount) {
        this.warningCount = warningCount;
    }

    public int getDuplicateDbCount() {
        return duplicateDbCount;
    }

    public void setDuplicateDbCount(int duplicateDbCount) {
        this.duplicateDbCount = duplicateDbCount;
    }

    public int getDuplicateCsvCount() {
        return duplicateCsvCount;
    }

    public void setDuplicateCsvCount(int duplicateCsvCount) {
        this.duplicateCsvCount = duplicateCsvCount;
    }

    public int getInvalidEmailCount() {
        return invalidEmailCount;
    }

    public void setInvalidEmailCount(int invalidEmailCount) {
        this.invalidEmailCount = invalidEmailCount;
    }

    public int getInvalidMobileCount() {
        return invalidMobileCount;
    }

    public void setInvalidMobileCount(int invalidMobileCount) {
        this.invalidMobileCount = invalidMobileCount;
    }

    public int getInvalidGstCount() {
        return invalidGstCount;
    }

    public void setInvalidGstCount(int invalidGstCount) {
        this.invalidGstCount = invalidGstCount;
    }

    public int getInvalidDobCount() {
        return invalidDobCount;
    }

    public void setInvalidDobCount(int invalidDobCount) {
        this.invalidDobCount = invalidDobCount;
    }

    public int getInvalidAnniversaryCount() {
        return invalidAnniversaryCount;
    }

    public void setInvalidAnniversaryCount(int invalidAnniversaryCount) {
        this.invalidAnniversaryCount = invalidAnniversaryCount;
    }

    public List<ImportValidationResultDto> getResults() {
        return results;
    }

    public void setResults(List<ImportValidationResultDto> results) {
        this.results = results;
    }
}
