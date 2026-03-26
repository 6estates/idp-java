package com.sixestates.rest.v1.model;

import java.io.InputStream;

public class FaasAnalysisRequest {
    private String fileName;
    private InputStream filesInputStream;
    private String customerType; // "1" or "2"
    private Integer informationType; // 0 or 1
    private String countryId;
    private String regionId;
    private String cifNumber;
    private String borrowerName;
    private Float loanAmount;
    private String applicationNumber;
    private String applicationDate; // "yyyy-MM-dd"
    private String currency; // "IDR" or "SGD"
    private Integer rateDateType; // 1, 2, or 4
    private Integer rateFrom; // 1, 2, or 3
    private String rateDate; // "yyyy-MM-dd"
    private Boolean automatic;
    private String hitl; // "false", "true", or "auto"
    private String industryType;
    private String industryBiCode;
    private String ebitdaRatio; // "xx%"
    private String relatedParties; // JSON string
    private String supplierBuyer; // JSON string
    private String checkAccountStr; // JSON string
    private String callbackUrl;
    private Boolean autoCallback;
    private Integer callbackMode; // 0, 1, or 2

    // Constructors
    public FaasAnalysisRequest() {
    }

    public FaasAnalysisRequest(InputStream filesInputStream, String fileName, String customerType, Integer informationType) {
        this.filesInputStream = filesInputStream;
        this.fileName = fileName;
        this.customerType = customerType;
        this.informationType = informationType;
    }

    // Getters and Setters
    public InputStream getFilesInputStream() {
        return filesInputStream;
    }

    public void setFilesInputStream(InputStream filesInputStream) {
        this.filesInputStream = filesInputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public Integer getInformationType() {
        return informationType;
    }

    public void setInformationType(Integer informationType) {
        this.informationType = informationType;
    }

    public String getCountryId() {
        return countryId;
    }

    public void setCountryId(String countryId) {
        this.countryId = countryId;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getCifNumber() {
        return cifNumber;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public Float getLoanAmount() {
        return loanAmount;
    }

    public void setLoanAmount(Float loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(String applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getRateDateType() {
        return rateDateType;
    }

    public void setRateDateType(Integer rateDateType) {
        this.rateDateType = rateDateType;
    }

    public Integer getRateFrom() {
        return rateFrom;
    }

    public void setRateFrom(Integer rateFrom) {
        this.rateFrom = rateFrom;
    }

    public String getRateDate() {
        return rateDate;
    }

    public void setRateDate(String rateDate) {
        this.rateDate = rateDate;
    }

    public Boolean getAutomatic() {
        return automatic;
    }

    public void setAutomatic(Boolean automatic) {
        this.automatic = automatic;
    }

    public String getHitl() {
        return hitl;
    }

    public void setHitl(String hitl) {
        this.hitl = hitl;
    }

    public String getIndustryType() {
        return industryType;
    }

    public void setIndustryType(String industryType) {
        this.industryType = industryType;
    }

    public String getIndustryBiCode() {
        return industryBiCode;
    }

    public void setIndustryBiCode(String industryBiCode) {
        this.industryBiCode = industryBiCode;
    }

    public String getEbitdaRatio() {
        return ebitdaRatio;
    }

    public void setEbitdaRatio(String ebitdaRatio) {
        this.ebitdaRatio = ebitdaRatio;
    }

    public String getRelatedParties() {
        return relatedParties;
    }

    public void setRelatedParties(String relatedParties) {
        this.relatedParties = relatedParties;
    }

    public String getSupplierBuyer() {
        return supplierBuyer;
    }

    public void setSupplierBuyer(String supplierBuyer) {
        this.supplierBuyer = supplierBuyer;
    }

    public String getCheckAccountStr() {
        return checkAccountStr;
    }

    public void setCheckAccountStr(String checkAccountStr) {
        this.checkAccountStr = checkAccountStr;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public Boolean getAutoCallback() {
        return autoCallback;
    }

    public void setAutoCallback(Boolean autoCallback) {
        this.autoCallback = autoCallback;
    }

    public Integer getCallbackMode() {
        return callbackMode;
    }

    public void setCallbackMode(Integer callbackMode) {
        this.callbackMode = callbackMode;
    }

    /**
     * Validate required parameters.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return filesInputStream != null &&
            customerType != null &&
            informationType != null;
    }

    /**
     * Validate customer type.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidCustomerType() {
        return "1".equals(customerType) || "2".equals(customerType);
    }

    /**
     * Validate information type.
     *
     * @return true if valid, false otherwise
     */
    public boolean isValidInformationType() {
        return informationType == 0 || informationType == 1;
    }
}
