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
public class FaasAnalysisResultFetcher {

    private FaasAnalysisResultFetcher() {}

    public static IdpResponse<Object> fetchResult(String applicationId) {
        return fetchResult(Idp.getRestClient(), applicationId);
    }

    private static IdpResponse<Object> fetchResult(final IdpRestClient client, String applicationId) {
        String url = Idp.getFaasAnalysisResultUrl() + applicationId;

        Request request = new Request(
            HttpMethod.GET,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("FaaS analysis result fetch failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }

            if (restException.getMessage() != null &&
                restException.getMessage().contains("Task is not done yet")) {
                throw new AnalysisNotReadyException("Task is not done yet, please retry later.");
            }

            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<Object>>() {});
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.ACCEPT, "application/json");
    }

    public static class AnalysisNotReadyException extends RuntimeException {
        public AnalysisNotReadyException(String message) {
            super(message);
        }

        public AnalysisNotReadyException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}