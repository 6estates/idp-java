package com.sixestates.rest.v1.quota;

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
 * @Data 2026/1/2
 */
public class QuotaFetcher {

    private QuotaFetcher() {}

    public static IdpResponse<QuotaData> fetchQuota() {
        return fetchQuota(Idp.getRestClient());
    }

    private static IdpResponse<QuotaData> fetchQuota(final IdpRestClient client) {
        String url = Idp.getQuotaUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Quota query failed: Unable to connect to server");
        } else if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        return JSON.parseObject(response.getContent(),
            new TypeReference<IdpResponse<QuotaData>>() {});
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "application/json");
    }

    public static class QuotaData {
        private String quota;

        public String getQuota() {
            return quota;
        }

        public void setQuota(String quota) {
            this.quota = quota;
        }

        public Integer getQuotaAsInteger() {
            try {
                return quota != null ? Integer.parseInt(quota) : null;
            } catch (NumberFormatException e) {
                return -1;
            }
        }

        public boolean hasQuota() {
            Integer quotaInt = getQuotaAsInteger();
            return quotaInt != null && quotaInt > 0;
        }

        @Override
        public String toString() {
            return "QuotaData{" +
                "quota='" + quota + '\'' +
                '}';
        }
    }

    public static Integer getRemainingQuota() {
        try {
            IdpResponse<QuotaData> response = fetchQuota();
            if (response != null && response.isSuccessful() && response.getData() != null) {
                return response.getData().getQuotaAsInteger();
            }
        } catch (Exception e) {
            // Ignore exception, return -1
        }
        return -1;
    }

    public static boolean hasAvailableQuota() {
        try {
            IdpResponse<QuotaData> response = fetchQuota();
            return response != null &&
                response.isSuccessful() &&
                response.getData() != null &&
                response.getData().hasQuota();
        } catch (Exception e) {
            return false;
        }
    }
}