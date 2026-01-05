package com.sixestates.rest.v1.docAgent;

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
import java.util.*;

import static com.sixestates.http.HttpClient.getBaseBuilder;

/**
 * @author yec
 * @description
 * @Data 2026/1/2
 */
public class DocumentAgentSubmitter {

    /**
     * Construct a new DocumentAgentSubmitter.
     */
    private DocumentAgentSubmitter() {}

    /**
     * Submit a file for document agent analysis using default client.
     *
     * @param request The document agent request parameters
     * @return API response with task application ID
     */
    public static IdpResponse<String> submit(DocumentAgentRequest request) {
        return submit(Idp.getRestClient(), request);
    }

    /**
     * Make the request to the Idp API to submit a document agent task.
     *
     * @param client HttpClient object
     * @param request The document agent request parameters
     * @return Requested object
     */
    private static IdpResponse<String> submit(final IdpRestClient client, DocumentAgentRequest request) {
        String url = Idp.getDocumentAgentAnalysisUrl();

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
            throw new ApiConnectionException("Document agent submission failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        // Parse the response using generic IdpResponse class
        // Note: data field is a simple string (application ID)
        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<String>>() {});
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
     * @param request Request to add post params to
     * @param agentRequest The document agent request parameters
     */
    private static void addPostParams(final Request request, DocumentAgentRequest agentRequest) {
        // Required parameters
        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("file", request.getInputStream(), ContentType.MULTIPART_FORM_DATA, agentRequest.getFileName());
        builder.addTextBody("fileName", agentRequest.getFileName());

        builder.addTextBody("flowCode", agentRequest.getFlowCode());

        // Optional parameters
        if (agentRequest.getCallback() != null && !agentRequest.getCallback().isEmpty()) {
            builder.addTextBody("callback", agentRequest.getCallback());
        }

        if (agentRequest.getAutoCallback() != null) {
            builder.addTextBody("autoCallback", agentRequest.getAutoCallback().toString());
        }

        if (agentRequest.getCallbackMode() != null) {
            builder.addTextBody("callbackMode", String.valueOf(agentRequest.getCallbackMode()));
        }

        if (agentRequest.getCallbackQaCodes() != null && !agentRequest.getCallbackQaCodes().isEmpty()) {
            builder.addTextBody("callbackQaCodes", agentRequest.getCallbackQaCodes());
        }

        if (agentRequest.getFileDocTypeListJson() != null && !agentRequest.getFileDocTypeListJson().isEmpty()) {
            builder.addTextBody("fileDocTypeList", agentRequest.getFileDocTypeListJson());
        }
        request.setHttpEntity(builder.build());
    }

    /**
     * DTO class for document agent request.
     */
    public static class DocumentAgentRequest {
        private String flowCode;
        private String fileName;
        private InputStream fileInputStream;
        private String callback;
        private Boolean autoCallback;
        private Integer callbackMode; // Currently only mode 1 is supported
        private String callbackQaCodes;
        private String fileDocTypeListJson; // JSON string for fileDocTypeList

        // Constructors
        public DocumentAgentRequest() {}

        public DocumentAgentRequest(String flowCode, InputStream fileInputStream, String fileName) {
            this.flowCode = flowCode;
            this.fileName = fileName;
            this.fileInputStream = fileInputStream;
        }

        public DocumentAgentRequest(String flowCode, InputStream fileInputStream, String fileName,String callback) {
            this.flowCode = flowCode;
            this.fileName = fileName;
            this.fileInputStream = fileInputStream;
            this.callback = callback;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFlowCode() {
            return flowCode;
        }

        public void setFlowCode(String flowCode) {
            this.flowCode = flowCode;
        }

        public InputStream getFileInputStream() {
            return fileInputStream;
        }

        public void setFileInputStream(InputStream fileInputStream) {
            this.fileInputStream = fileInputStream;
        }

        public String getCallback() {
            return callback;
        }

        public void setCallback(String callback) {
            this.callback = callback;
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

        public String getCallbackQaCodes() {
            return callbackQaCodes;
        }

        public void setCallbackQaCodes(String callbackQaCodes) {
            this.callbackQaCodes = callbackQaCodes;
        }

        public String getFileDocTypeListJson() {
            return fileDocTypeListJson;
        }

        public void setFileDocTypeListJson(String fileDocTypeListJson) {
            this.fileDocTypeListJson = fileDocTypeListJson;
        }

        /**
         * Validate required parameters.
         *
         * @return true if valid, false otherwise
         */
        public boolean isValid() {
            return flowCode != null && !flowCode.isEmpty() &&
                fileInputStream != null;
        }

        /**
         * Validate flow code format.
         * Flow codes typically follow patterns like "DAG24", "FLOW001", etc.
         *
         * @return true if valid, false otherwise
         */
        public boolean isValidFlowCode() {
            if (flowCode == null || flowCode.isEmpty()) {
                return false;
            }
            // Basic validation: should contain letters and optionally numbers
            return flowCode.matches("^[A-Za-z0-9]+$");
        }

        /**
         * Validate callback mode.
         *
         * @return true if valid, false otherwise
         */
        public boolean isValidCallbackMode() {
            if (callbackMode == null) {
                return true; // Optional parameter
            }
            return callbackMode == 1; // Currently only mode 1 is supported
        }

        /**
         * Validate fileDocTypeList JSON.
         *
         * @return true if valid JSON, false otherwise
         */
        public boolean isValidFileDocTypeListJson() {
            if (fileDocTypeListJson == null || fileDocTypeListJson.isEmpty()) {
                return true; // Optional parameter
            }

            try {
                // Try to parse as JSON array
                JSON.parseArray(fileDocTypeListJson);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        /**
         * Get formatted flow code description.
         *
         * @return Flow code description
         */
        public String getFlowCodeDescription() {
            if (flowCode == null) {
                return "Unknown flow";
            }

            // This could be enhanced with a lookup table for known flow codes
            return "Document Agent Flow: " + flowCode;
        }
    }

    /**
     * Helper class for file document type mapping.
     */
    public static class FileDocType {
        private String fileName;
        private String zipName; // Optional, for files within ZIP
        private String fileType;
        private Integer fileTypeFrom; // 1: from 6e definition, 2: custom type

        // Constructors
        public FileDocType() {}

        public FileDocType(String fileName, String fileType, Integer fileTypeFrom) {
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileTypeFrom = fileTypeFrom;
        }

        public FileDocType(String zipName, String fileName, String fileType, Integer fileTypeFrom) {
            this.zipName = zipName;
            this.fileName = fileName;
            this.fileType = fileType;
            this.fileTypeFrom = fileTypeFrom;
        }

        // Getters and Setters
        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getZipName() {
            return zipName;
        }

        public void setZipName(String zipName) {
            this.zipName = zipName;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public Integer getFileTypeFrom() {
            return fileTypeFrom;
        }

        public void setFileTypeFrom(Integer fileTypeFrom) {
            this.fileTypeFrom = fileTypeFrom;
        }

        /**
         * Check if this is for a file within a ZIP.
         *
         * @return true if file is within ZIP, false otherwise
         */
        public boolean isWithinZip() {
            return zipName != null && !zipName.isEmpty();
        }

        /**
         * Check if file type is from 6E definition.
         *
         * @return true if fileTypeFrom == 1, false otherwise
         */
        public boolean isFrom6eDefinition() {
            return fileTypeFrom != null && fileTypeFrom == 1;
        }

        /**
         * Check if file type is custom.
         *
         * @return true if fileTypeFrom == 2, false otherwise
         */
        public boolean isCustomType() {
            return fileTypeFrom != null && fileTypeFrom == 2;
        }

        /**
         * Convert to JSON string.
         *
         * @return JSON representation
         */
        public String toJsonString() {
            return JSON.toJSONString(this);
        }
    }

    /**
     * Helper class for building document agent requests.
     */
    public static class RequestBuilder {
        private DocumentAgentRequest request;
        private List<FileDocType> fileDocTypes;

        public RequestBuilder(String flowCode, InputStream fileInputStream) {
            this.request = new DocumentAgentRequest(flowCode, fileInputStream, request.getFileName());
            this.fileDocTypes = new ArrayList<>();
        }

        public RequestBuilder withCallback(String callback) {
            request.setCallback(callback);
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

        public RequestBuilder withCallbackQaCodes(String callbackQaCodes) {
            request.setCallbackQaCodes(callbackQaCodes);
            return this;
        }

        /**
         * Add a file document type mapping.
         *
         * @param fileDocType File document type mapping
         * @return RequestBuilder instance
         */
        public RequestBuilder addFileDocType(FileDocType fileDocType) {
            this.fileDocTypes.add(fileDocType);
            return this;
        }

        /**
         * Add a file document type mapping for a standalone file.
         *
         * @param fileName File name
         * @param fileType File type (e.g., "CBKS", "BL")
         * @param fileTypeFrom 1: from 6e definition, 2: custom type
         * @return RequestBuilder instance
         */
        public RequestBuilder addFileDocType(String fileName, String fileType, Integer fileTypeFrom) {
            this.fileDocTypes.add(new FileDocType(fileName, fileType, fileTypeFrom));
            return this;
        }

        /**
         * Add a file document type mapping for a file within a ZIP.
         *
         * @param zipName ZIP file name
         * @param fileName File name within ZIP
         * @param fileType File type (e.g., "CBKS", "BL")
         * @param fileTypeFrom 1: from 6e definition, 2: custom type
         * @return RequestBuilder instance
         */
        public RequestBuilder addFileDocType(String zipName, String fileName, String fileType, Integer fileTypeFrom) {
            this.fileDocTypes.add(new FileDocType(zipName, fileName, fileType, fileTypeFrom));
            return this;
        }

        public DocumentAgentRequest build() {
            if (!request.isValid()) {
                throw new IllegalStateException("flowCode and fileInputStream are required");
            }
            if (!request.isValidFlowCode()) {
                throw new IllegalStateException("flowCode must contain only letters and numbers");
            }
            if (!request.isValidCallbackMode()) {
                throw new IllegalStateException("callbackMode must be 1 (currently only mode 1 is supported)");
            }

            // Build fileDocTypeList JSON if any file doc types were added
            if (!fileDocTypes.isEmpty()) {
                String json = JSON.toJSONString(fileDocTypes);
                request.setFileDocTypeListJson(json);

                if (!request.isValidFileDocTypeListJson()) {
                    throw new IllegalStateException("fileDocTypeList is not valid JSON");
                }
            }

            return request;
        }
    }

    /**
     * Validate that application ID is suitable for database storage.
     * According to API documentation, applicationId length should be at least 32.
     *
     * @param applicationId Application ID to validate
     * @return true if valid for database storage, false otherwise
     */
    public static boolean isValidForDatabaseStorage(String applicationId) {
        return applicationId != null && applicationId.length() >= 32;
    }

    /**
     * Get common flow code examples.
     * Note: Actual flow codes need to be obtained from 6E admin.
     *
     * @return Map of example flow codes and descriptions
     */
    public static Map<String, String> getExampleFlowCodes() {
        Map<String, String> examples = new LinkedHashMap<>();
        examples.put("DAG24", "Document Agent Flow 24");
        examples.put("FLOW001", "General Document Processing Flow");
        examples.put("INVOICE_FLOW", "Invoice Processing Flow");
        examples.put("CONTRACT_FLOW", "Contract Analysis Flow");
        return examples;
    }

    /**
     * Get common file types.
     *
     * @return List of common file types
     */
    public static List<String> getCommonFileTypes() {
        return Arrays.asList(
            "CBKS",      // Bank Statement
            "BL",        // Bill of Lading
            "INV",       // Invoice
            "PO",        // Purchase Order
            "DO",        // Delivery Order
            "PL",        // Packing List
            "CONTRACT",  // Contract
            "RESUME",    // Resume/CV
            "ID_CARD",   // ID Card
            "PASSPORT"   // Passport
        );
    }

    /**
     * Create file document type mapping for multiple files.
     *
     * @param files Map of filename to filetype
     * @param fileTypeFrom 1: from 6e definition, 2: custom type
     * @return List of FileDocType objects
     */
    public static List<FileDocType> createFileDocTypeMapping(Map<String, String> files, Integer fileTypeFrom) {
        List<FileDocType> mappings = new ArrayList<>();

        for (Map.Entry<String, String> entry : files.entrySet()) {
            FileDocType mapping = new FileDocType(
                entry.getKey(),  // fileName
                entry.getValue(), // fileType
                fileTypeFrom
            );
            mappings.add(mapping);
        }

        return mappings;
    }

    /**
     * Create file document type mapping for files within a ZIP.
     *
     * @param zipName ZIP file name
     * @param files Map of filename (within ZIP) to filetype
     * @param fileTypeFrom 1: from 6e definition, 2: custom type
     * @return List of FileDocType objects
     */
    public static List<FileDocType> createFileDocTypeMappingForZip(String zipName,
                                                                   Map<String, String> files,
                                                                   Integer fileTypeFrom) {
        List<FileDocType> mappings = new ArrayList<>();

        for (Map.Entry<String, String> entry : files.entrySet()) {
            FileDocType mapping = new FileDocType(
                zipName,         // zipName
                entry.getKey(),  // fileName
                entry.getValue(), // fileType
                fileTypeFrom
            );
            mappings.add(mapping);
        }

        return mappings;
    }

    /**
     * Convert list of FileDocType to JSON string.
     *
     * @param fileDocTypes List of FileDocType objects
     * @return JSON string
     */
    public static String convertFileDocTypesToJson(List<FileDocType> fileDocTypes) {
        return JSON.toJSONString(fileDocTypes);
    }
}
