package com.sixestates.rest.v1;

import com.alibaba.fastjson.JSONObject;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import org.apache.http.HttpHeaders;

public class ResultExtractor {

    private ResultExtractor() {}

    /**
     * Execute a request using default client by taskId.
     *
     * @param taskId The taskId of a submitted task
     * @return Response json string
     */
    public static String extractResultByTaskid(final String taskId) {
        if(taskId == null) {
            throw new ApiException("taskId can not be null");
        }

        return run(Idp.getRestClient(), taskId);
    }

    /**
     * Execute a request using default client by taskId.
     *
     * @param client HttpClient object
     * @return Response json string
     */
    private static String run(final IdpRestClient client, String taskId) {
        Request request = new Request(
                HttpMethod.GET,
                Idp.getExtractUrl() + taskId
        );
        request.addHeaderParam(HttpHeaders.ACCEPT, "*/*");
        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Message creation failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        String respJson = response.getContent();
        String status = JSONObject.parseObject(respJson).getJSONObject("data").getString("status");

        if(status.equals("Init") || status.equals("Doing")) {
            throw new ApiException("Task " + status + ", please request again after 30 seconds");
        } else if(status.equals("Fail")) {
            throw new ApiException("Task " + status + ", please contact us to resolve the problem");
        }
        return respJson;
    }

}
