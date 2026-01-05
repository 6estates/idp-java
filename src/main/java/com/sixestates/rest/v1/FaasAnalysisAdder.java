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
 * @description
 * @Data 2025/12/30
 */
public class FaasAnalysisAdder {

    private FaasAnalysisAdder() {
    }

    public static IdpResponse<String> addFiles(FileAdditionRequest addRequest) {
        return addFiles(Idp.getRestClient(), addRequest);
    }

    private static IdpResponse<String> addFiles(final IdpRestClient client, FileAdditionRequest addRequest) {
        String url = Idp.getFaasAnalysisAdditionUrl();

        Request request = new Request(
            HttpMethod.POST,
            url,
            addRequest.getFilesInputStream()
        );
        request.setIsSubmit(true);
        addHeaderParams(request);
        addPostParams(request, addRequest);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("FaaS analysis file addition failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<String>>() {
            });
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
    }

    private static void addPostParams(final Request request, FileAdditionRequest addRequest) {
        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("files", request.getInputStream(), ContentType.MULTIPART_FORM_DATA, addRequest.getFileName());
        builder.addTextBody("fileName", addRequest.getFileName());

        builder.addTextBody("applicationId", addRequest.getApplicationId());

        if (addRequest.getHitl() != null && !addRequest.getHitl().isEmpty()) {
            builder.addTextBody("hitl", addRequest.getHitl());
        }
        request.setHttpEntity(builder.build());
    }

    public static class FileAdditionRequest {
        private String applicationId;
        private String fileName;
        private InputStream filesInputStream;
        private String hitl;

        public FileAdditionRequest() {
        }

        public FileAdditionRequest(String applicationId, InputStream filesInputStream, String fileName) {
            this.fileName = fileName;
            this.applicationId = applicationId;
            this.filesInputStream = filesInputStream;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public InputStream getFilesInputStream() {
            return filesInputStream;
        }

        public void setFilesInputStream(InputStream filesInputStream) {
            this.filesInputStream = filesInputStream;
        }

        public String getHitl() {
            return hitl;
        }

        public void setHitl(String hitl) {
            this.hitl = hitl;
        }
    }
}