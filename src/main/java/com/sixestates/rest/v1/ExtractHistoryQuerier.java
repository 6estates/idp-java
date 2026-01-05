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
import com.sixestates.type.faas.HistoryQueryParams;
import org.apache.http.HttpHeaders;

import java.util.List;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class ExtractHistoryQuerier {

    private ExtractHistoryQuerier() {}

    public static HistoryTaskListResponse query(HistoryQueryParams queryParams) {
        return query(Idp.getRestClient(), queryParams);
    }

    private static HistoryTaskListResponse query(final IdpRestClient client, HistoryQueryParams queryParams) {
        String url = Idp.getHistoryListUrl();

        Request request = new Request(
            HttpMethod.POST,
            url,
            null
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, queryParams);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("History query failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }
        return JSON.parseObject(response.getContent(), HistoryTaskListResponse.class);
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    private static void addPostParams(final Request request, HistoryQueryParams queryParams) {
        request.addPostParam("page", String.valueOf(queryParams.getPage()));
        request.addPostParam("limit", String.valueOf(queryParams.getLimit()));
        request.addPostParam("sortOrder", queryParams.getSortOrder());
//        if (queryParams.getSortOrder() != null && !queryParams.getSortOrder().isEmpty()) {
//            request.addPostParam("sortOrder", queryParams.getSortOrder());
//        }
        if (queryParams.getSortColumn() != null && !queryParams.getSortColumn().isEmpty()) {
            request.addPostParam("sortColumn", queryParams.getSortColumn());
        }

        if (queryParams.getTaskStatus() != null) {
            request.addPostParam("taskStatus", String.valueOf(queryParams.getTaskStatus()));
        }

        if (queryParams.getStatus() != null) {
            request.addPostParam("status", String.valueOf(queryParams.getStatus()));
        }

        if (queryParams.getFileTypeCode() != null && !queryParams.getFileTypeCode().isEmpty()) {
            request.addPostParam("fileTypeCode", queryParams.getFileTypeCode());
        }

        if (queryParams.getSource() != null) {
            request.addPostParam("source", String.valueOf(queryParams.getSource()));
        }

        if (queryParams.getEdited() != null) {
            request.addPostParam("edited", queryParams.getEdited().toString());
        }

        if (queryParams.getHitl() != null) {
            request.addPostParam("hitl", queryParams.getHitl().toString());
        }

        if (queryParams.getFileName() != null && !queryParams.getFileName().isEmpty()) {
            request.addPostParam("fileName", queryParams.getFileName());
        }

        if (queryParams.getStartCreateTime() != null && !queryParams.getStartCreateTime().isEmpty()) {
            request.addPostParam("startCreateTime", queryParams.getStartCreateTime());
        }

        if (queryParams.getEndCreateTime() != null && !queryParams.getEndCreateTime().isEmpty()) {
            request.addPostParam("endCreateTime", queryParams.getEndCreateTime());
        }
    }


    public static class HistoryTaskListResponse {
        private Integer status;
        private Integer errorCode;
        private String message;
        private HistoryTaskListData data;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Integer getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(Integer errorCode) {
            this.errorCode = errorCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public HistoryTaskListData getData() {
            return data;
        }

        public void setData(HistoryTaskListData data) {
            this.data = data;
        }

        public boolean isSuccessful() {
            return status != null && status == 200 && errorCode != null && errorCode == 0;
        }
    }

    public static class HistoryTaskListData {
        private List<HistoryTaskDTO> result;
        private Integer total;

        public List<HistoryTaskDTO> getResult() {
            return result;
        }

        public void setResult(List<HistoryTaskDTO> result) {
            this.result = result;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }
    }

    public static class HistoryTaskDTO {
        private String id;
        private String applicationId;
        private String fileName;
        private Integer pageCount;
        private Long createTime;
        private Long lastUpdateTime;
        private Boolean edited;
        private String fileTypeName;
        private Boolean hitl;
        private Integer source;
        private String email;
        private Integer taskStatus;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Integer getPageCount() {
            return pageCount;
        }

        public void setPageCount(Integer pageCount) {
            this.pageCount = pageCount;
        }

        public Long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(Long createTime) {
            this.createTime = createTime;
        }

        public Long getLastUpdateTime() {
            return lastUpdateTime;
        }

        public void setLastUpdateTime(Long lastUpdateTime) {
            this.lastUpdateTime = lastUpdateTime;
        }

        public Boolean getEdited() {
            return edited;
        }

        public void setEdited(Boolean edited) {
            this.edited = edited;
        }

        public String getFileTypeName() {
            return fileTypeName;
        }

        public void setFileTypeName(String fileTypeName) {
            this.fileTypeName = fileTypeName;
        }

        public Boolean getHitl() {
            return hitl;
        }

        public void setHitl(Boolean hitl) {
            this.hitl = hitl;
        }

        public Integer getSource() {
            return source;
        }

        public void setSource(Integer source) {
            this.source = source;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getTaskStatus() {
            return taskStatus;
        }

        public void setTaskStatus(Integer taskStatus) {
            this.taskStatus = taskStatus;
        }
    }
}