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
import com.sixestates.type.FAASTaskInfo;
import com.sixestates.type.FileInfo;
import com.sixestates.type.HttpKey;
import com.sixestates.type.TaskDTO;
import com.sixestates.utils.CollectionUtils;
import com.sixestates.utils.StringUtils;
import org.apache.http.HttpHeaders;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kechen, 09/08/24.
 */
public class FAASSubmitter {
    /**
     * Submit FAAS task request using default client.
     *
     * @return Requested object
     */
    public static TaskDTO submitFAASTask(FAASTaskInfo taskInfo) {
        return submit(Idp.getRestClient(), taskInfo);
    }

    /**
     * Make the request to the FAAS API.
     *
     * @param client HttpClient object
     * @return Requested object
     */
    private static TaskDTO submit(final IdpRestClient client, FAASTaskInfo taskInfo) {

        Request request = new Request(
                HttpMethod.POST,
                Idp.getSubmitFAASUrl()
        );
        request.setIsSubmit(true);
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");
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
     * Add the requested post parameters to the Request.
     *
     * @param request Request to add post params to
     */
    private static void addPostParams(final Request request, FAASTaskInfo taskInfo) {
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
        fileInfoMap.put("files", fileInfos);
        request.setFileInfoMap(fileInfoMap);
        Field[] declaredFields = taskInfo.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            Object value = null;
            try {
                value = declaredField.get(taskInfo);
            } catch (IllegalAccessException e) {
                //do nothing
            }
            if (value == null) {
                continue;
            }
            if (declaredField.isAnnotationPresent(HttpKey.class)) {
                HttpKey httpKey = declaredField.getAnnotation(HttpKey.class);
                if (httpKey.ignore()) {
                    continue;
                }
                String key = StringUtils.isNotEmpty(httpKey.value()) ? httpKey.value() : declaredField.getName();
                request.addPostParam(key, String.valueOf(value));
            } else {
                request.addPostParam(declaredField.getName(), String.valueOf(value));
            }
        }
    }


}
