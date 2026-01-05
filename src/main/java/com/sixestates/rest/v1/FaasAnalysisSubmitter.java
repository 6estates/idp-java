package com.sixestates.rest.v1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import com.sixestates.type.IdpResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.InputStream;
import java.util.List;

import static com.sixestates.http.HttpClient.getBaseBuilder;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class FaasAnalysisSubmitter {

    /**
     * Construct a new FaasAnalysisSubmitter.
     */
    private FaasAnalysisSubmitter() {
    }

    /**
     * Submit a ZIP file for FaaS analysis using default client.
     *
     * @param analysisRequest The FaaS analysis request parameters
     * @return API response with analysis task ID
     */
    public static IdpResponse<String> submit(FaasAnalysisRequest analysisRequest) {
        return submit(Idp.getRestClient(), analysisRequest);
    }

    /**
     * Make the request to the Idp API to submit a FaaS analysis task.
     *
     * @param client          HttpClient object
     * @param analysisRequest The FaaS analysis request parameters
     * @return Requested object
     */
    private static IdpResponse<String> submit(final IdpRestClient client, FaasAnalysisRequest analysisRequest) {
        String url = Idp.getFaasAnalysisUrl();

        Request request = new Request(
            HttpMethod.POST,
            url,
            analysisRequest.getFilesInputStream()
        );
        request.setIsSubmit(true);
        addHeaderParams(request);
        addPostParams(request, analysisRequest);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("FaaS analysis submission failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        // Parse the response using generic IdpResponse class
        // Note: data field is a simple string (task ID) in this API
        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<String>>() {
            });
    }

    /**
     * Add the requested header parameters to the Request.
     *
     * @param request Request to add header params to
     */
    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
    }

    /**
     * Add the requested post parameters to the Request.
     *
     * @param request         Request to add post params to
     * @param analysisRequest The FaaS analysis request parameters
     */
    private static void addPostParams(final Request request, FaasAnalysisRequest analysisRequest) {
        // Required parameters
        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("files", request.getInputStream(), ContentType.MULTIPART_FORM_DATA, analysisRequest.getFileName());
        builder.addTextBody("customerType", analysisRequest.getCustomerType());
        builder.addTextBody("informationType", String.valueOf(analysisRequest.getInformationType()));
        builder.addTextBody("fileName", analysisRequest.getFileName());

        // Optional parameters
        if (analysisRequest.getCountryId() != null && !analysisRequest.getCountryId().isEmpty()) {
            builder.addTextBody("countryId", analysisRequest.getCountryId());
        }

        if (analysisRequest.getRegionId() != null && !analysisRequest.getRegionId().isEmpty()) {
            builder.addTextBody("regionId", analysisRequest.getRegionId());
        }

        if (analysisRequest.getCifNumber() != null && !analysisRequest.getCifNumber().isEmpty()) {
            builder.addTextBody("cifNumber", analysisRequest.getCifNumber());
        }

        if (analysisRequest.getBorrowerName() != null && !analysisRequest.getBorrowerName().isEmpty()) {
            builder.addTextBody("borrowerName", analysisRequest.getBorrowerName());
        }

        if (analysisRequest.getLoanAmount() != null) {
            builder.addTextBody("loanAmount", String.valueOf(analysisRequest.getLoanAmount()));
        }

        if (analysisRequest.getApplicationNumber() != null && !analysisRequest.getApplicationNumber().isEmpty()) {
            builder.addTextBody("applicationNumber", analysisRequest.getApplicationNumber());
        }

        if (analysisRequest.getApplicationDate() != null && !analysisRequest.getApplicationDate().isEmpty()) {
            builder.addTextBody("applicationDate", analysisRequest.getApplicationDate());
        }

        if (analysisRequest.getCurrency() != null && !analysisRequest.getCurrency().isEmpty()) {
            builder.addTextBody("currency", analysisRequest.getCurrency());
        }

        if (analysisRequest.getRateDateType() != null) {
            builder.addTextBody("rateDateType", String.valueOf(analysisRequest.getRateDateType()));
        }

        if (analysisRequest.getRateFrom() != null) {
            builder.addTextBody("rateFrom", String.valueOf(analysisRequest.getRateFrom()));
        }

        if (analysisRequest.getRateDate() != null && !analysisRequest.getRateDate().isEmpty()) {
            builder.addTextBody("rateDate", analysisRequest.getRateDate());
        }

        if (analysisRequest.getAutomatic() != null) {
            builder.addTextBody("automatic", analysisRequest.getAutomatic().toString());
        }

        if (analysisRequest.getHitl() != null && !analysisRequest.getHitl().isEmpty()) {
            builder.addTextBody("hitl", analysisRequest.getHitl());
        }

        if (analysisRequest.getIndustryType() != null && !analysisRequest.getIndustryType().isEmpty()) {
            builder.addTextBody("industryType", analysisRequest.getIndustryType());
        }

        if (analysisRequest.getIndustryBiCode() != null && !analysisRequest.getIndustryBiCode().isEmpty()) {
            builder.addTextBody("industryBiCode", analysisRequest.getIndustryBiCode());
        }

        if (analysisRequest.getEbitdaRatio() != null && !analysisRequest.getEbitdaRatio().isEmpty()) {
            builder.addTextBody("ebitdaRatio", analysisRequest.getEbitdaRatio());
        }

        if (analysisRequest.getRelatedParties() != null && !analysisRequest.getRelatedParties().isEmpty()) {
            builder.addTextBody("relatedParties", analysisRequest.getRelatedParties());
        }

        if (analysisRequest.getSupplierBuyer() != null && !analysisRequest.getSupplierBuyer().isEmpty()) {
            builder.addTextBody("supplierBuyer", analysisRequest.getSupplierBuyer());
        }

        if (analysisRequest.getCheckAccountStr() != null && !analysisRequest.getCheckAccountStr().isEmpty()) {
            builder.addTextBody("checkAccountStr", analysisRequest.getCheckAccountStr());
        }

        if (analysisRequest.getCallbackUrl() != null && !analysisRequest.getCallbackUrl().isEmpty()) {
            builder.addTextBody("callbackUrl", analysisRequest.getCallbackUrl());
        }

        if (analysisRequest.getAutoCallback() != null) {
            builder.addTextBody("autoCallback", analysisRequest.getAutoCallback().toString());
        }

        if (analysisRequest.getCallbackMode() != null) {
            builder.addTextBody("callbackMode", String.valueOf(analysisRequest.getCallbackMode()));
        }

        request.setHttpEntity(builder.build());
    }

    /**
     * DTO class for FaaS analysis request.
     */
    public static class FaasAnalysisRequest {
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

    /**
     * Helper classes for JSON parameter construction
     */

    /**
     * Related party information for JSON construction
     */
    public static class RelatedParty {
        private String relatedType; // "ORG_TYPE" or "OCCUPATION"
        private Integer orgType; // 101-105 for company, 201-206 for individual
        private String name;
        private List<RelatedPartyAccount> children;

        // Getters and Setters
        public String getRelatedType() {
            return relatedType;
        }

        public void setRelatedType(String relatedType) {
            this.relatedType = relatedType;
        }

        public Integer getOrgType() {
            return orgType;
        }

        public void setOrgType(Integer orgType) {
            this.orgType = orgType;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<RelatedPartyAccount> getChildren() {
            return children;
        }

        public void setChildren(List<RelatedPartyAccount> children) {
            this.children = children;
        }

        public String toJsonString() {
            return JSON.toJSONString(this);
        }
    }

    public static class RelatedPartyAccount {
        private String accountHolderName;
        private String accountNumber;
        private String bankName;

        // Getters and Setters
        public String getAccountHolderName() {
            return accountHolderName;
        }

        public void setAccountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
    }

    /**
     * Supplier/Buyer information for JSON construction
     */
    public static class SupplierBuyer {
        private Integer businessRelationship; // 1, 2, or 3
        private Integer longRelationship; // 1, 2, 3, or 4
        private String companyName;
        private List<SupplierBuyerAccount> children;

        // Getters and Setters
        public Integer getBusinessRelationship() {
            return businessRelationship;
        }

        public void setBusinessRelationship(Integer businessRelationship) {
            this.businessRelationship = businessRelationship;
        }

        public Integer getLongRelationship() {
            return longRelationship;
        }

        public void setLongRelationship(Integer longRelationship) {
            this.longRelationship = longRelationship;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public List<SupplierBuyerAccount> getChildren() {
            return children;
        }

        public void setChildren(List<SupplierBuyerAccount> children) {
            this.children = children;
        }

        public String toJsonString() {
            return JSON.toJSONString(this);
        }
    }

    public static class SupplierBuyerAccount {
        private String accountName;
        private String accountNumber;
        private String bankName;

        // Getters and Setters
        public String getAccountName() {
            return accountName;
        }

        public void setAccountName(String accountName) {
            this.accountName = accountName;
        }

        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }
    }

    /**
     * Check account information for JSON construction
     */
    public static class CheckAccount {
        private String accountNumber;
        private String bankName;
        private String accountHolderName;

        // Getters and Setters
        public String getAccountNumber() {
            return accountNumber;
        }

        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        public String getBankName() {
            return bankName;
        }

        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

        public String getAccountHolderName() {
            return accountHolderName;
        }

        public void setAccountHolderName(String accountHolderName) {
            this.accountHolderName = accountHolderName;
        }

        public String toJsonString() {
            return JSON.toJSONString(this);
        }
    }

    /**
     * Helper class for building FaaS analysis requests.
     */
    public static class RequestBuilder {
        private FaasAnalysisRequest request;

        public RequestBuilder(InputStream filesInputStream, String fileName, String customerType, Integer informationType) {
            this.request = new FaasAnalysisRequest(filesInputStream, fileName, customerType, informationType);
        }

        public RequestBuilder withCountryId(String countryId) {
            request.setCountryId(countryId);
            return this;
        }

        public RequestBuilder withRegionId(String regionId) {
            request.setRegionId(regionId);
            return this;
        }

        public RequestBuilder withCifNumber(String cifNumber) {
            request.setCifNumber(cifNumber);
            return this;
        }

        public RequestBuilder withBorrowerName(String borrowerName) {
            request.setBorrowerName(borrowerName);
            return this;
        }

        public RequestBuilder withLoanAmount(Float loanAmount) {
            request.setLoanAmount(loanAmount);
            return this;
        }

        public RequestBuilder withApplicationNumber(String applicationNumber) {
            request.setApplicationNumber(applicationNumber);
            return this;
        }

        public RequestBuilder withApplicationDate(String applicationDate) {
            request.setApplicationDate(applicationDate);
            return this;
        }

        public RequestBuilder withCurrency(String currency) {
            request.setCurrency(currency);
            return this;
        }

        public RequestBuilder withRateDateType(Integer rateDateType) {
            request.setRateDateType(rateDateType);
            return this;
        }

        public RequestBuilder withRateFrom(Integer rateFrom) {
            request.setRateFrom(rateFrom);
            return this;
        }

        public RequestBuilder withRateDate(String rateDate) {
            request.setRateDate(rateDate);
            return this;
        }

        public RequestBuilder withAutomatic(Boolean automatic) {
            request.setAutomatic(automatic);
            return this;
        }

        public RequestBuilder withHitl(String hitl) {
            request.setHitl(hitl);
            return this;
        }

        public RequestBuilder withIndustryType(String industryType) {
            request.setIndustryType(industryType);
            return this;
        }

        public RequestBuilder withIndustryBiCode(String industryBiCode) {
            request.setIndustryBiCode(industryBiCode);
            return this;
        }

        public RequestBuilder withEbitdaRatio(String ebitdaRatio) {
            request.setEbitdaRatio(ebitdaRatio);
            return this;
        }

        public RequestBuilder withRelatedPartiesJson(String relatedPartiesJson) {
            request.setRelatedParties(relatedPartiesJson);
            return this;
        }

        public RequestBuilder withSupplierBuyerJson(String supplierBuyerJson) {
            request.setSupplierBuyer(supplierBuyerJson);
            return this;
        }

        public RequestBuilder withCheckAccountStrJson(String checkAccountStrJson) {
            request.setCheckAccountStr(checkAccountStrJson);
            return this;
        }

        public RequestBuilder withCallbackUrl(String callbackUrl) {
            request.setCallbackUrl(callbackUrl);
            return this;
        }

        public RequestBuilder withAutoCallback(Boolean autoCallback) {
            request.setAutoCallback(autoCallback);
            return this;
        }

        public RequestBuilder withCallbackMode(Integer callbackMode) {
            request.setCallbackMode(callbackMode);
            return this;
        }

        public FaasAnalysisRequest build() {
            if (!request.isValid()) {
                throw new IllegalStateException("filesInputStream, customerType, and informationType are required");
            }
            return request;
        }
    }
}
