package com.sixestates.rest.v1.digitization;

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
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.InputStream;

import static com.sixestates.http.HttpClient.getBaseBuilder;

/**
 * Digitization API Submitter (OCR)
 */
public class DigitizationApi {

    private DigitizationApi() {}

    // --- 7.1.1 Asynchronous Submit ---
    public static IdpResponse<String> submit(InputStream fileStream, String fileName) {
        IdpRestClient client = Idp.getRestClient();
        String url = Idp.getDigitizationUrl();

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.setIsSubmit(true);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");

        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("file", fileStream, ContentType.MULTIPART_FORM_DATA, fileName);

        apiRequest.setHttpEntity(builder.build());
        return execute(client, apiRequest, new TypeReference<IdpResponse<String>>() {});
    }

    // --- 7.1.2 Query Status ---
    public static IdpResponse<Integer> queryStatus(String applicationId) {
        String url = Idp.getDigitizationStatusUrl();
        Request request = new Request(HttpMethod.POST, url);
        request.addPostParam("applicationId", applicationId);
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        return execute(Idp.getRestClient(), request, new TypeReference<IdpResponse<Integer>>(){});
    }

    // --- 7.1.3 Download/Query Result ---
    public static Response downloadResult(DigitizationResultRequest requestParamer) {
        String url = Idp.getDigitizationExportUrl();

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        apiRequest.addPostParam("applicationId", requestParamer.getApplicationId());
        apiRequest.addPostParam("type", requestParamer.getType().toString());
        if (requestParamer.getFontSize() != null) {
            apiRequest.addPostParam("fontSize", requestParamer.getFontSize().toString());
        }
        IdpRestClient client = Idp.getRestClient();
        Response response = client.request(apiRequest);

        if (response == null) {
            throw new ApiConnectionException("Digitization export failed: Unable to connect to server");
        }
        // 如果不是 200，尝试解析错误信息
        if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Export Error");
        }
        return response;
    }



    private static <T> T execute(IdpRestClient client, Request apiRequest, TypeReference<T> typeReference) {
        Response response = client.request(apiRequest);
        if (response == null) {
            throw new ApiConnectionException("API request failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Server Error");
        }
        return JSON.parseObject(response.getContent(), typeReference);
    }

    // --- Request Model for 7.1.3 ---
    public static class DigitizationResultRequest {
        private String applicationId;
        private Integer type; // 1:Word, 2:Txt, 3:Json
        private Integer fontSize;

        public DigitizationResultRequest(String applicationId, Integer type) {
            this.applicationId = applicationId;
            this.type = type;
        }

        public String getApplicationId() { return applicationId; }
        public void setApplicationId(String applicationId) { this.applicationId = applicationId; }
        public Integer getType() { return type; }
        public void setType(Integer type) { this.type = type; }
        public Integer getFontSize() { return fontSize; }
        public void setFontSize(Integer fontSize) { this.fontSize = fontSize; }
    }
}
