package com.sixestates.rest.v1.docAgent;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.sixestates.Idp;
import com.sixestates.enums.DocAgentStatusForApiEnum;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import com.sixestates.type.IdpResponse;
import org.apache.http.HttpHeaders;

/**
 * @author yec
 * @description
 * @Data 2026/1/2
 */
public class DocumentAgentStatusFetcher {

    private DocumentAgentStatusFetcher() {}

    public static IdpResponse<DocAgentStatusData> fetchStatus(String applicationId) {
        return fetchStatus(Idp.getRestClient(), applicationId);
    }

    private static IdpResponse<DocAgentStatusData> fetchStatus(final IdpRestClient client, String applicationId) {
        String url = Idp.getDocumentAgentStatusUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, applicationId);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Document agent status fetch failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<DocAgentStatusData>>() {});
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    private static void addPostParams(final Request request, String applicationId) {
        request.addPostParam("applicationId", applicationId);
    }

    public static class DocAgentStatusData {
        private String fileName;
        private String taskFlowName;
        private String uploadUserEmail;
        private Long uploadTime;
        private Integer statusCode;
        private String status;
        private String errorMsg;
        private Long finishTime;
        private Integer errorTaskCount;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getTaskFlowName() {
            return taskFlowName;
        }

        public void setTaskFlowName(String taskFlowName) {
            this.taskFlowName = taskFlowName;
        }

        public String getUploadUserEmail() {
            return uploadUserEmail;
        }

        public void setUploadUserEmail(String uploadUserEmail) {
            this.uploadUserEmail = uploadUserEmail;
        }

        public Long getUploadTime() {
            return uploadTime;
        }

        public void setUploadTime(Long uploadTime) {
            this.uploadTime = uploadTime;
        }

        public Integer getStatusCode() {
            return statusCode;
        }

        public void setStatusCode(Integer statusCode) {
            this.statusCode = statusCode;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getErrorMsg() {
            return errorMsg;
        }

        public void setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public Long getFinishTime() {
            return finishTime;
        }

        public void setFinishTime(Long finishTime) {
            this.finishTime = finishTime;
        }

        public Integer getErrorTaskCount() {
            return errorTaskCount;
        }

        public void setErrorTaskCount(Integer errorTaskCount) {
            this.errorTaskCount = errorTaskCount;
        }

        public DocAgentStatusForApiEnum getStatusEnum() {
            if (statusCode == null) {
                return DocAgentStatusForApiEnum.ON_PROCESS;
            }
            return DocAgentStatusForApiEnum.convertFromDocAgentStatusCode(statusCode);
        }

        public boolean isInProcess() {
            return getStatusEnum() == DocAgentStatusForApiEnum.ON_PROCESS;
        }

        public boolean isFinished() {
            return getStatusEnum() == DocAgentStatusForApiEnum.FINISHED;
        }

        public boolean isFailed() {
            return getStatusEnum() == DocAgentStatusForApiEnum.FAILED;
        }

        public boolean hasErrorTasks() {
            return errorTaskCount != null && errorTaskCount > 0;
        }

        @Override
        public String toString() {
            return "DocAgentStatusData{" +
                "fileName='" + fileName + '\'' +
                ", taskFlowName='" + taskFlowName + '\'' +
                ", uploadUserEmail='" + uploadUserEmail + '\'' +
                ", uploadTime=" + uploadTime +
                ", statusCode=" + statusCode +
                ", status='" + status + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", finishTime=" + finishTime +
                ", errorTaskCount=" + errorTaskCount +
                '}';
        }
    }
}