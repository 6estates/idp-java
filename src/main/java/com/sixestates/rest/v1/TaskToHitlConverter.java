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

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class TaskToHitlConverter {

    private TaskToHitlConverter() {}

    public static IdpResponse<HitlConversionData> convert(HitlConversionRequest hitlRequest) {
        return convert(Idp.getRestClient(), hitlRequest);
    }

    private static IdpResponse<HitlConversionData> convert(final IdpRestClient client, HitlConversionRequest hitlRequest) {
        String url = Idp.getToHitlUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, hitlRequest);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Task to HITL conversion failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<HitlConversionData>>() {});
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    private static void addPostParams(final Request request, HitlConversionRequest hitlRequest) {
        if (hitlRequest.getApplicationId() != null && !hitlRequest.getApplicationId().isEmpty()) {
            request.addPostParam("applicationId", hitlRequest.getApplicationId());
        } else {
            throw new IllegalArgumentException("applicationId must be provided");
        }

        if (hitlRequest.getCallback() != null && !hitlRequest.getCallback().isEmpty()) {
            request.addPostParam("callback", hitlRequest.getCallback());
        }

        if (hitlRequest.getAutoCallback() != null) {
            request.addPostParam("autoCallback", hitlRequest.getAutoCallback().toString());
        }

        if (hitlRequest.getCallbackMode() != null) {
            request.addPostParam("callbackMode", hitlRequest.getCallbackMode().toString());
        }
    }

    public static class HitlConversionRequest {
        private String applicationId;
        private String callback;
        private Boolean autoCallback;
        private Integer callbackMode;

        public HitlConversionRequest() {}

        public HitlConversionRequest(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
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
    }

    public static class HitlConversionData {
        private Boolean success;
        private String message;

        public Boolean getSuccess() {
            return success;
        }

        public void setSuccess(Boolean success) {
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        @Override
        public String toString() {
            return "HitlConversionData{" +
                "success=" + success +
                ", message='" + message + '\'' +
                '}';
        }
    }
}