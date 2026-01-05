package com.sixestates.rest.v1.splitext;

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
 * @description
 * @Data 2025/12/30
 */
public class SplitExtractionSubmitter {

    private SplitExtractionSubmitter() {}

    public static IdpResponse<String> submit(SplitExtractionRequest request) {
        return submit(Idp.getRestClient(), request);
    }

    private static IdpResponse<String> submit(final IdpRestClient client, SplitExtractionRequest request) {
        String url = Idp.getSplitExtractionUrl();

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
            throw new ApiConnectionException("Split extraction submission failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<String>>() {});
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
    }

    private static void addPostParams(final Request request, SplitExtractionRequest extractionRequest) {

        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("file", request.getInputStream(), ContentType.MULTIPART_FORM_DATA, extractionRequest.getFileName());
        builder.addTextBody("fileName", extractionRequest.getFileName());

        builder.addTextBody("groupId", String.valueOf(extractionRequest.getGroupId()));

        if (extractionRequest.getLang() != null && !extractionRequest.getLang().isEmpty()) {
            builder.addTextBody("lang", extractionRequest.getLang());
        }

        if (extractionRequest.getHitl() != null) {
            builder.addTextBody("hitl", extractionRequest.getHitl().toString());
        }

        if (extractionRequest.getExtractMode() != null) {
            builder.addTextBody("extractMode", String.valueOf(extractionRequest.getExtractMode()));
        }
        request.setHttpEntity(builder.build());
    }

    public static class SplitExtractionRequest {
        private String fileName;
        private InputStream fileInputStream;
        private String lang;
        private Boolean hitl;
        private Integer extractMode;
        private Integer groupId;

        public SplitExtractionRequest() {}

        public SplitExtractionRequest(InputStream fileInputStream,String fileName, Integer groupId) {
            this.fileInputStream = fileInputStream;
            this.fileName = fileName;
            this.groupId = groupId;
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

        public Boolean getHitl() {
            return hitl;
        }

        public void setHitl(Boolean hitl) {
            this.hitl = hitl;
        }

        public Integer getExtractMode() {
            return extractMode;
        }

        public void setExtractMode(Integer extractMode) {
            this.extractMode = extractMode;
        }

        public Integer getGroupId() {
            return groupId;
        }

        public void setGroupId(Integer groupId) {
            this.groupId = groupId;
        }
    }
}