package com.sixestates.rest.v1;


import com.alibaba.fastjson.JSON;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.InvalidRequestException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import com.sixestates.type.FileInfo;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import com.sixestates.utils.CollectionUtils;
import com.sixestates.utils.StringUtils;
import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
                Idp.getSubmitUrl()
        );
        request.setIsSubmit(true);
        addHeaderParams(request);
        addPostParams(request, taskInfo);
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
        Map<String, List<FileInfo>> fileInfoMap = new HashMap<>();
        List<File> files = taskInfo.getFiles();
        List<FileInfo> fileInfos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(files)) {
            for (File file : files) {
                try {
                    fileInfos.add(new FileInfo(file.getName(), new FileInputStream(file)));
                } catch (FileNotFoundException e) {
                    throw new InvalidRequestException("file not found", e);
                }
            }
        } else if(!CollectionUtils.isEmpty(taskInfo.getFileInfos())) {
            fileInfos.addAll(taskInfo.getFileInfos());
        }
        fileInfoMap.put("file", fileInfos);
        request.setFileInfoMap(fileInfoMap);
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

        if (taskInfo.getAutoCallback() != null) {
            request.addPostParam("autoCallback", String.valueOf(taskInfo.getAutoCallback()));
        }

        if(taskInfo.isHitl()) {
            request.addPostParam("hitl", "true");
        }
        
        if(taskInfo.getAutoChecks() != 0) {
            request.addPostParam("autoChecks", String.valueOf(taskInfo.getAutoChecks()));
        }

        if(taskInfo.getExtractMode() != null) {
            request.addPostParam("extractMode", String.valueOf(taskInfo.getExtractMode()));
        }

        if (taskInfo.getFileTypeFrom() != null) {
            request.addPostParam("fileTypeFrom", String.valueOf(taskInfo.getFileTypeFrom()));
        }

        if(StringUtils.isNotEmpty(taskInfo.getIncludingFieldCodes())) {
            request.addPostParam("includingFieldCodes", taskInfo.getIncludingFieldCodes());
        }

        if (StringUtils.isNotEmpty(taskInfo.getRemark())) {
            request.addPostParam("remark", taskInfo.getRemark());
        }

    }

}
