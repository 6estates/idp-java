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

import static com.sixestates.http.HttpClient.getBaseBuilder;

/**
 * @author yec
 * @description 同步卡片字段提取
 * @Data 2026/1/2
 */
public class SyncCardExtractor {

    /**
     * Construct a new SyncCardExtractor.
     */
    private SyncCardExtractor() {
    }

    /**
     * Submit a card file for synchronous field extraction using default client.
     *
     * @param request The synchronous card extraction request parameters
     * @return API response with extraction result data
     */
    public static IdpResponse<CardExtractionResult> extract(CardExtractionRequest request) {
        return extract(Idp.getRestClient(), request);
    }

    /**
     * Make the request to the Idp API to extract fields from card file.
     *
     * @param client  HttpClient object
     * @param request The synchronous card extraction request parameters
     * @return Requested object
     */
    private static IdpResponse<CardExtractionResult> extract(final IdpRestClient client, CardExtractionRequest request) {
        String url = Idp.getSyncCardExtractionUrl();

        Request apiRequest = new Request(
            HttpMethod.POST,
            url,
            request.getFileInputStream()
        );
        apiRequest.setIsSubmit(true);
        addHeaderParams(apiRequest);
        addPostParams(apiRequest, request);

        Response response = client.request(apiRequest);

        if (response == null) {
            throw new ApiConnectionException("Synchronous card extraction failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        // Parse the response using generic IdpResponse class
        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<CardExtractionResult>>() {
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
     * @param request           Request to add post params to
     * @param extractionRequest The synchronous card extraction request parameters
     */
    private static void addPostParams(final Request request, CardExtractionRequest extractionRequest) {
        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("file", request.getInputStream(), ContentType.MULTIPART_FORM_DATA, extractionRequest.getFileName());

        builder.addTextBody("fileType", extractionRequest.getFileType());
        // Optional parameters
        if (extractionRequest.getLang() != null && !extractionRequest.getLang().isEmpty()) {
            builder.addTextBody("lang", extractionRequest.getLang());
        }
        request.setHttpEntity(builder.build());
    }

    /**
     * DTO class for synchronous card extraction request.
     */
    public static class CardExtractionRequest {
        private InputStream fileInputStream;
        private String fileName;
        private String lang;
        private String fileType;

        // Constructors
        public CardExtractionRequest() {
        }

        public CardExtractionRequest(InputStream fileInputStream, String fileName, String fileType) {
            this.fileInputStream = fileInputStream;
            this.fileName = fileName;
            this.fileType = fileType;
        }


        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public InputStream getFileInputStream() {
            return fileInputStream;
        }

        public void setFileInputStream(InputStream fileInputStream) {
            this.fileInputStream = fileInputStream;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }
    }

    /**
     * DTO class for card extraction result data.
     */
    public static class CardExtractionResult {
        private String taskFileName;
        private java.util.List<ExtractedField> fields;
        private String lang;
        private String fileType;
        private String applicationId;
        private String taskId;

        // Getters and Setters
        public String getTaskFileName() {
            return taskFileName;
        }

        public void setTaskFileName(String taskFileName) {
            this.taskFileName = taskFileName;
        }

        public java.util.List<ExtractedField> getFields() {
            return fields;
        }

        public void setFields(java.util.List<ExtractedField> fields) {
            this.fields = fields;
        }

        public String getLang() {
            return lang;
        }

        public void setLang(String lang) {
            this.lang = lang;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getTaskId() {
            return taskId;
        }

        public void setTaskId(String taskId) {
            this.taskId = taskId;
        }
    }

    /**
     * DTO class for extracted field.
     */
    public static class ExtractedField {
        private Integer no;
        private Double extractionConfidence;
        private String fieldCode;
        private String type;
        private String value;
        private String fieldName;

        // Getters and Setters
        public Integer getNo() {
            return no;
        }

        public void setNo(Integer no) {
            this.no = no;
        }

        public Double getExtractionConfidence() {
            return extractionConfidence;
        }

        public void setExtractionConfidence(Double extractionConfidence) {
            this.extractionConfidence = extractionConfidence;
        }

        public String getFieldCode() {
            return fieldCode;
        }

        public void setFieldCode(String fieldCode) {
            this.fieldCode = fieldCode;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }
    }

}