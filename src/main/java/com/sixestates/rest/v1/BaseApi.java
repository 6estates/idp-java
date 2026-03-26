package com.sixestates.rest.v1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;

/**
 * Base class for API clients providing common execution methods.
 */
public abstract class BaseApi {

    protected BaseApi() {}

    protected static <T> T execute(Request apiRequest, TypeReference<T> typeReference) {
        return execute(Idp.getRestClient(), apiRequest, typeReference);
    }

    protected static <T> T execute(IdpRestClient client, Request apiRequest, TypeReference<T> typeReference) {
        Response response = client.request(apiRequest);
        if (response == null) {
            throw new ApiConnectionException("API request failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Server Error");
        }
        return JSON.parseObject(response.getContent(), typeReference);
    }

    protected static Response executeForResponse(Request apiRequest) {
        IdpRestClient client = Idp.getRestClient();
        Response response = client.request(apiRequest);
        if (response == null) {
            throw new ApiConnectionException("API request failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Export Error");
        }
        return response;
    }
}
