package com.sixestates.rest.v1;

import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.exception.RestException;
import com.sixestates.http.HttpMethod;
import com.sixestates.http.IdpRestClient;
import com.sixestates.http.Request;
import com.sixestates.http.Response;
import org.apache.http.HttpHeaders;

import java.io.*;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class FaasAnalysisExporter {

    private FaasAnalysisExporter() {}

    public static byte[] exportResult(String applicationId) {
        return exportResult(Idp.getRestClient(), applicationId);
    }

    public static File exportResultToFile(String applicationId, String outputFilePath) throws IOException {
        return exportResultToFile(Idp.getRestClient(), applicationId, outputFilePath);
    }

    private static byte[] exportResult(final IdpRestClient client, String applicationId) {
        String url = Idp.getFaasAnalysisExportUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, applicationId);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("FaaS analysis export failed: Unable to connect to server");
        }

        if (response.getStatusCode() == 400) {
            String responseContent = response.getContent();
            if (responseContent != null && responseContent.contains("Task is not done yet")) {
                throw new AnalysisNotReadyException("Task is not done yet, please retry later.");
            }
        }

        if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }
            throw new ApiException(restException);
        }

        InputStream resultStream = response.getStream();
        if (resultStream == null) {
            throw new ApiException("No data received from server");
        }

        try {
            return readAllBytes(resultStream);
        } catch (IOException e) {
            throw new ApiException("Failed to read response data", e);
        }
    }

    private static File exportResultToFile(final IdpRestClient client, String applicationId, String outputFilePath)
        throws IOException {
        byte[] data = exportResult(client, applicationId);

        File outputFile = new File(outputFilePath);
        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            fos.write(data);
        }

        return outputFile;
    }

    private static void addHeaderParams(final Request request) {
        request.addHeaderParam(HttpHeaders.CONTENT_TYPE, "application/json");
        request.addHeaderParam(HttpHeaders.ACCEPT, "application/octet-stream");
    }

    private static void addPostParams(final Request request, String applicationId) {
        request.addPostParam("applicationId", applicationId);
    }

    private static byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int bytesRead;

        while ((bytesRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
        }

        return buffer.toByteArray();
    }

    public static class AnalysisNotReadyException extends RuntimeException {
        public AnalysisNotReadyException(String message) {
            super(message);
        }

        public AnalysisNotReadyException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}