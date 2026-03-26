package com.sixestates.rest.v1.fsagent;

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
 * FS Agent API Submitter
 */
public class FsAgentApi {

    private FsAgentApi() {}

    // --- 5.1.1 Asynchronous Submit ---
    public static IdpResponse<String> submit(FsAgentRequest request) {
        IdpRestClient client = Idp.getRestClient();
        String url = Idp.getFsAgentSubmitUrl();

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.setIsSubmit(true);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");

        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("files", request.getFileStream(), ContentType.MULTIPART_FORM_DATA, request.getFileName());

        if (request.getHitl() != null) {
            builder.addTextBody("hitl", String.valueOf(request.getHitl()));
        }
        if (request.getCustomerType() != null) {
            builder.addTextBody("customerType", String.valueOf(request.getCustomerType()));
        }

        apiRequest.setHttpEntity(builder.build());
        return execute(client, apiRequest, new TypeReference<IdpResponse<String>>() {});
    }

    // --- 5.1.2 Query Status ---
    public static IdpResponse<FsAgentStatus> queryStatus(String applicationId) {
        String url = Idp.getFsAgentStatusUrl();
        Request request = new Request(HttpMethod.POST, url);
        request.addPostParam("applicationId", applicationId);
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        return execute(Idp.getRestClient(), request, new TypeReference<IdpResponse<FsAgentStatus>>() {
        });
    }

    // --- 5.1.3 Export Result ---
    public static Response downloadResult(String applicationId) {
        String url = Idp.getFsAgentExportUrl();

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        apiRequest.addPostParam("applicationId", applicationId);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        Response response = Idp.getRestClient().request(apiRequest);
        if (response == null || !IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            throw new ApiException("FS Agent Export failed");
        }
        return response;
    }

    private static <T> T execute(IdpRestClient client, Request apiRequest, TypeReference<T> typeReference) {
        Response response = client.request(apiRequest);
        if (response == null) throw new ApiConnectionException("Connection failed");
        if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Server Error");
        }
        return JSON.parseObject(response.getContent(), typeReference);
    }

    // --- Data Models ---
    public static class FsAgentRequest {
        private InputStream fileStream;
        private String fileName;
        private Boolean hitl = false;
        private Integer customerType = 1;

        public FsAgentRequest(InputStream fileStream, String fileName) {
            this.fileStream = fileStream;
            this.fileName = fileName;
        }
        // Getters and Setters
        public InputStream getFileStream() { return fileStream; }
        public String getFileName() { return fileName; }
        public Boolean getHitl() { return hitl; }
        public void setHitl(Boolean hitl) { this.hitl = hitl; }
        public Integer getCustomerType() { return customerType; }
        public void setCustomerType(Integer customerType) { this.customerType = customerType; }
    }

    public static class FsAgentStatus {
        private String fileName;
        private Integer statusCode; // 10: Process, 11: Finished, -1: Failed
        private String status;
        private String errorMsg;
        private Long uploadTime;
        private Long finishTime;

        // Getters and Setters
        public Integer getStatusCode() { return statusCode; }
        public void setStatusCode(Integer statusCode) { this.statusCode = statusCode; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getErrorMsg() { return errorMsg; }
        public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    }
}