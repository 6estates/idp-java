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

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class SplitExtractionStatusFetcher {

    private SplitExtractionStatusFetcher() {}

    public static IdpResponse<Integer> fetchStatus(String applicationId) {
        return fetchStatus(Idp.getRestClient(), applicationId);
    }

    private static IdpResponse<Integer> fetchStatus(final IdpRestClient client, String applicationId) {
        String url = Idp.getSplitExtractionStatusUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, applicationId);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Split extraction status fetch failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<Integer>>() {});
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    private static void addPostParams(final Request request, String applicationId) {
        request.addPostParam("applicationId", applicationId);
    }
}