package com.sixestates.rest.v1;

import com.alibaba.fastjson.JSONObject;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.*;
import com.sixestates.type.ResultDTO;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultExtractor {
    private static final Logger logger = LoggerFactory.getLogger(ResultExtractor.class);
    private ResultExtractor() {}

    /**
     * Execute a request using default client by taskId.
     *
     * @param taskId The taskId of a submitted task
     * @return Response json string
     */
    public static ResultDTO extractResultByTaskid(final String taskId) {
        if(taskId == null) {
            throw new ApiException("taskId can not be null");
        }

        return run(Idp.getRestClient(), taskId);
    }

    /**
     * Execute a request using default client by taskId.
     *
     * @param client HttpClient object
     * @return resultDto response dto
     */
    private static ResultDTO run(final IdpRestClient client, String taskId) {
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
        String taskStatus = JSONObject.parseObject(respJson).getJSONObject("data").getString("taskStatus");
        ResultDTO resultDto = ResultDTO.builder().taskStatus(taskStatus).respJson(respJson).build();
        if(taskStatus.equals("Init") || taskStatus.equals("Doing")) {
            logger.debug("TaskId " + taskId + ": " + taskStatus + ", please request again after 30 seconds");
            return resultDto;
        } else if(taskStatus.equals("Fail")) {
            throw new ApiException("TaskId " + taskId + ": " + taskStatus + ", please contact us to resolve the problem");
        }
        logger.debug("TaskId " + taskId + ": " + taskStatus);
        return resultDto;
    }
}
