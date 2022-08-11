package com.sixestates.rest.v1;


import com.alibaba.fastjson.JSON;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import org.apache.http.HttpHeaders;


public class ExtractSubmitter {

    /**
     * Construct a new ExtractSubmitter.
     */
    private ExtractSubmitter() {}


    /**
     * Execute a request using default client.
     *
     * @return Requested object
     */
    public static TaskDTO submit(TaskInfo taskInfo) {
        return submit(Idp.getRestClient(), taskInfo);
    }

    /**
     * Make the request to the Idp API to submit a file extraction task.
     *
     * @param client HttpClient object
     * @return Requested object
     */
    private static TaskDTO submit(final IdpRestClient client,TaskInfo taskInfo) {

        Request request = new Request(
                HttpMethod.POST,
                Idp.getSubmitUrl(),
                taskInfo.getInputStream()
        );
        request.setIsSubmit(true);
        addHeaderParams(request);
        addPostParams(request,taskInfo);
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
        return JSON.parseObject(response.getContent(), TaskDTO.class);
    }

    /**
     * Add the requested header parameters to the Request.
     *
     * @param request Request to add post params to
     */
    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
    }

    /**
     * Add the requested post parameters to the Request.
     *
     * @param request Request to add post params to
     */
    private static void addPostParams(final Request request, TaskInfo taskInfo) {
        request.addPostParam("fileName", taskInfo.getFileName());
        request.addPostParam("filePath", taskInfo.getFilePath());
        request.addPostParam("fileType", taskInfo.getFileType());
        if(taskInfo.getCustomer() != null) {
            request.addPostParam("customer", taskInfo.getCustomer());
        }

        if(taskInfo.getCustomerParam() != null) {
            request.addPostParam("customerParam", taskInfo.getCustomerParam());
        }

        if(taskInfo.getCallback() != null) {
            request.addPostParam("callback", taskInfo.getCallback());
            request.addPostParam("callbackMode", String.valueOf(taskInfo.getCallbackMode()));
        }

        if(taskInfo.isHitl()) {
            request.addPostParam("hitl", "true");
        }
        
        if(taskInfo.getAutoChecks() != 0) {
            request.addPostParam("autoChecks", String.valueOf(taskInfo.getAutoChecks()));
        }
    }

}
