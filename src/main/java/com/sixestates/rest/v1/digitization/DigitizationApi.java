package com.sixestates.rest.v1.digitization;

import com.alibaba.fastjson.TypeReference;
import com.sixestates.Idp;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import com.sixestates.rest.v1.BaseApi;
import com.sixestates.rest.v1.model.DigitizationResultRequest;
import com.sixestates.type.IdpResponse;
import org.apache.http.HttpHeaders;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.InputStream;

import static com.sixestates.http.HttpClient.getBaseBuilder;

/**
 * Digitization API Submitter (OCR)
 */
public class DigitizationApi extends BaseApi {

    private DigitizationApi() {}

    // --- 7.1.1 Asynchronous Submit ---
    public static IdpResponse<String> submit(InputStream fileStream, String fileName) {
        IdpRestClient client = Idp.getRestClient();
        String url = Idp.getDigitizationUrl();

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.setIsSubmit(true);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");

        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addBinaryBody("file", fileStream, ContentType.MULTIPART_FORM_DATA, fileName);

        apiRequest.setHttpEntity(builder.build());
        return execute(apiRequest, new TypeReference<IdpResponse<String>>() {});
    }

    // --- 7.1.2 Query Status ---
    public static IdpResponse<Integer> queryStatus(String applicationId) {
        String url = Idp.getDigitizationStatusUrl();
        Request request = new Request(HttpMethod.POST, url);
        request.addPostParam("applicationId", applicationId);
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        return execute(request, new TypeReference<IdpResponse<Integer>>(){});
    }

    // --- 7.1.3 Download/Query Result ---
    public static Response downloadResult(DigitizationResultRequest requestParamer) {
        String url = Idp.getDigitizationExportUrl();

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        apiRequest.addPostParam("applicationId", requestParamer.getApplicationId());
        apiRequest.addPostParam("type", requestParamer.getType().toString());
        if (requestParamer.getFontSize() != null) {
            apiRequest.addPostParam("fontSize", requestParamer.getFontSize().toString());
        }
        return executeForResponse(apiRequest);
    }
}
