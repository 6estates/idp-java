package com.sixestates.rest.v1;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.InvalidRequestException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import com.sixestates.type.*;
import com.sixestates.utils.CollectionUtils;
import com.sixestates.utils.Lists;
import com.sixestates.utils.StringUtils;
import org.apache.http.Header;
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
 * @author kechen, 10/10/24.
 */
public class DocAgentApi {
    public static final String CONTENT_DISPOSITION = "Content-Disposition";

    /**
     * Submit Doc Agent task request using default client.
     *
     * @return Requested object
     */
    public static TaskDTO submit(DocAgentInfo taskInfo) {
        return submit(Idp.getRestClient(), taskInfo);
    }

    /**
     * Get task status
     *
     * @param applicationId
     * @return
     */
    public static TaskResult<DocAgentStatusInfo> getStatus(String applicationId) {
        if (!StringUtils.isNotEmpty(applicationId)) {
            throw new ApiException("applicationId can not be empty");
        }

        return run(Idp.getRestClient(), applicationId);
    }

    /**
     * Export task file
     *
     * @param applicationId
     * @return
     */
    public static FileInfo export(String applicationId) {
        if (!StringUtils.isNotEmpty(applicationId)) {
            throw new ApiException("applicationId can not be empty");
        }

        return export(Idp.getRestClient(), applicationId);
    }

    /**
     * Make the request to the FAAS API.
     *
     * @param client HttpClient object
     * @return Requested object
     */
    private static TaskDTO submit(final IdpRestClient client, DocAgentInfo taskInfo) {

        Request request = new Request(
                HttpMethod.POST,
                Idp.submitDocAgentUrl
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
    private static void addPostParams(final Request request, DocAgentInfo taskInfo) {
        Map<String, List<FileInfo>> fileInfoMap = new HashMap<>();
        List<File> files = Lists.newArrayList(taskInfo.getFile());
        List<FileInfo> fileInfos = new ArrayList<>();
        if (!CollectionUtils.isEmpty(files)) {
            for (File file : files) {
                try {
                    fileInfos.add(new FileInfo(file.getName(), new FileInputStream(file)));
                } catch (FileNotFoundException e) {
                    throw new InvalidRequestException("file not found", e);
                }
            }
        } else if (taskInfo.getFileInfo() != null) {
            fileInfos.add(taskInfo.getFileInfo());
        }
        fileInfoMap.put("file", fileInfos);
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


    /**
     * Execute a request using default client by applicationId.
     *
     * @param client HttpClient object
     * @return resultDto response dto
     */
    private static TaskResult<DocAgentStatusInfo> run(final IdpRestClient client, String applicationId) {
        Request request = new Request(
                HttpMethod.POST,
                Idp.getDocAgentTaskStatusUrl + applicationId
        );
        request.addHeaderParam(HttpHeaders.ACCEPT, "application/json");
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

        return convertResponse(response.getContent(), DocAgentStatusInfo.class);
    }

    private static <T> TaskResult<T> convertResponse(String response, Class classz) {
        TaskResult<T> responseObj = JSON.parseObject(response, new TypeReference<TaskResult<T>>(classz) {

        });
        if (responseObj == null) {
            throw new ApiException(String.format("Fail to get big data response : %s", response));
        }
        return responseObj;
    }

    private static FileInfo export(final IdpRestClient client, String applicationId) {
        Request request = new Request(
                HttpMethod.POST,
                Idp.exportDocAgentTaskUrl + applicationId
        );
        request.addHeaderParam(HttpHeaders.ACCEPT, "*/*");
        request.setIsSubmit(true);
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

        Header[] headers = response.getHeaders();
        String fileName = null;
        for (Header header : headers) {
            if (CONTENT_DISPOSITION.equalsIgnoreCase(header.getName())) {
                String contentDisposition = header.getValue();
                if (contentDisposition.contains("filename=")) {
                    fileName = contentDisposition.substring(contentDisposition.indexOf("filename=") + 9);
                    fileName = fileName.replaceAll("\"", ""); // Remove any surrounding quotes
                }
            }
        }

        return new FileInfo(fileName, response.getStream());
    }


}
