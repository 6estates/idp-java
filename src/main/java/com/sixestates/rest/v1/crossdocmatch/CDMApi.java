package com.sixestates.rest.v1.crossdocmatch;


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
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.sixestates.http.HttpClient.getBaseBuilder;

/**
 * Cross Document Matching (CDM) API Submitter
 * Based on SplitExtractionSubmitter style
 */
public class CDMApi {

    private CDMApi() {}

    // --- 8.1.1 Asynchronous Submit ---
    public static IdpResponse<String> submit(CDMSubmitRequest request) {
        IdpRestClient client = Idp.getRestClient();
        String url = Idp.getcDMSubmitUrl(); // 假设 Idp 类中有此方法，或直接使用字符串路径

        Request apiRequest = new Request(HttpMethod.POST, url);
        apiRequest.setIsSubmit(true);
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, "multipart/form-data");

        MultipartEntityBuilder builder = getBaseBuilder();
        builder.addTextBody("matchingGroupCode", request.getMatchingGroupCode());
        if (request.getMergeFile() != null) {
            builder.addTextBody("mergeFile", String.valueOf(request.getMergeFile()));
        }
        if (request.getHitl() != null) {
            builder.addTextBody("hitl", String.valueOf(request.getHitl()));
        }

        // 处理 fileParams 数组
        List<FileParam> params = request.getFileParams();
        for (int i = 0; i < params.size(); i++) {
            FileParam fp = params.get(i);
            String prefix = "fileParams[" + i + "].";
            builder.addBinaryBody(prefix + "file", fp.getFileInputStream(), ContentType.MULTIPART_FORM_DATA, fp.getFileName());
            if (fp.getDocType() != null) {
                builder.addTextBody(prefix + "docType", fp.getDocType());
            }
            if (fp.getDetectionMode() != null) {
                builder.addTextBody(prefix + "detectionMode", String.valueOf(fp.getDetectionMode()));
            }
        }

        apiRequest.setHttpEntity(builder.build());
        return execute(client, apiRequest, new TypeReference<IdpResponse<String>>() {});
    }

    // --- 8.1.2 Query Status ---
    public static IdpResponse<Integer> queryStatus(String applicationId) {
        String url = Idp.getcDMStatusUrl();
        Request request = new Request(HttpMethod.POST, url);
        request.addPostParam("applicationId", applicationId);
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        return execute(Idp.getRestClient(), request, new TypeReference<IdpResponse<Integer>>() {});
    }

    // --- 8.1.3 & 8.1.4 Download Result (Excel/ZIP) ---
    // 注意：下载接口通常返回 Response 的流，此处返回 Response 供调用者处理保存
    public static Response downloadResult(String applicationId, boolean isZip) {
        // 1. 获取对应的 URL
        String url = isZip ? Idp.getcDMExportZipUrl() : Idp.getcDMExportExcelUrl();

        // 2. 构建 Request
        Request apiRequest = new Request(HttpMethod.POST, url);
        // 设置 Header 为 JSON
        apiRequest.addHeaderParam(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());

        // 3. 构建 JSON Body
        apiRequest.addPostParam("applicationId", applicationId);

        // 4. 执行请求
        IdpRestClient client = Idp.getRestClient();
        Response response = client.request(apiRequest);

        // 5. 仿照 SplitExtractionSubmitter 的错误处理风格
        if (response == null) {
            throw new ApiConnectionException("Download failed: Unable to connect to server");
        }

        // 注意：下载接口如果返回 400/500，通常 body 里是错误 JSON，如果是 200 则是文件流
        if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Server Error during download");
        }

        return response;
    }

    private static <T> T execute(IdpRestClient client, Request apiRequest, TypeReference<T> typeReference) {
        Response response = client.request(apiRequest);
        if (response == null) {
            throw new ApiConnectionException("CDM request failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            throw new ApiException(restException != null ? restException.getMessage() : "Server Error");
        }
        return JSON.parseObject(response.getContent(), typeReference);
    }

    // --- Request Models ---

    public static class CDMSubmitRequest {
        private String matchingGroupCode;
        private Boolean mergeFile = false;
        private Boolean hitl = false;
        private List<FileParam> fileParams = new ArrayList<>();

        public void addFileParam(FileParam param) { this.fileParams.add(param); }
        // Getters and Setters...
        public String getMatchingGroupCode() { return matchingGroupCode; }
        public void setMatchingGroupCode(String matchingGroupCode) { this.matchingGroupCode = matchingGroupCode; }
        public Boolean getMergeFile() { return mergeFile; }
        public void setMergeFile(Boolean mergeFile) { this.mergeFile = mergeFile; }
        public Boolean getHitl() { return hitl; }
        public void setHitl(Boolean hitl) { this.hitl = hitl; }
        public List<FileParam> getFileParams() { return fileParams; }
    }

    public static class FileParam {
        private InputStream fileInputStream;
        private String fileName;
        private String docType;
        private Integer detectionMode;

        public FileParam(InputStream is, String name) { this.fileInputStream = is; this.fileName = name; }
        public InputStream getFileInputStream() { return fileInputStream; }
        public String getFileName() { return fileName; }
        public String getDocType() { return docType; }
        public void setDocType(String docType) { this.docType = docType; }
        public Integer getDetectionMode() { return detectionMode; }
        public void setDetectionMode(Integer detectionMode) { this.detectionMode = detectionMode; }
    }
}