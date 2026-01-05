package com.sixestates.rest.v1;

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

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class FaasAnalysisStatusFetcher {

    private FaasAnalysisStatusFetcher() {}

    public static IdpResponse<FaasAnalysisStatusData> fetchStatus(String applicationId) {
        return fetchStatus(Idp.getRestClient(), applicationId);
    }

    private static IdpResponse<FaasAnalysisStatusData> fetchStatus(final IdpRestClient client, String applicationId) {
        String url = Idp.getFaasAnalysisStatusUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, applicationId);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("FaaS analysis status fetch failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<FaasAnalysisStatusData>>() {});
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    private static void addPostParams(final Request request, String applicationId) {
        request.addPostParam("applicationId", applicationId);
    }

    public static class FaasAnalysisStatusData {
        private String applicationId;
        private String analysisStatus;
        private Integer analysisStatusCode;
        private String analysisErrorMsg;

        public String getApplicationId() {
            return applicationId;
        }

        public void setApplicationId(String applicationId) {
            this.applicationId = applicationId;
        }

        public String getAnalysisStatus() {
            return analysisStatus;
        }

        public void setAnalysisStatus(String analysisStatus) {
            this.analysisStatus = analysisStatus;
        }

        public Integer getAnalysisStatusCode() {
            return analysisStatusCode;
        }

        public void setAnalysisStatusCode(Integer analysisStatusCode) {
            this.analysisStatusCode = analysisStatusCode;
        }

        public String getAnalysisErrorMsg() {
            return analysisErrorMsg;
        }

        public void setAnalysisErrorMsg(String analysisErrorMsg) {
            this.analysisErrorMsg = analysisErrorMsg;
        }

        public boolean isDone() {
            return analysisStatusCode != null && analysisStatusCode == 2;
        }

        public boolean isFailed() {
            return analysisStatusCode != null &&
                (analysisStatusCode == 3 || analysisStatusCode == 4 || analysisStatusCode == 5);
        }

        @Override
        public String toString() {
            return "FaasAnalysisStatusData{" +
                "applicationId='" + applicationId + '\'' +
                ", analysisStatus='" + analysisStatus + '\'' +
                ", analysisStatusCode=" + analysisStatusCode +
                ", analysisErrorMsg='" + analysisErrorMsg + '\'' +
                '}';
        }
    }
}