package com.sixestates.rest.v1.splitext;

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

import java.io.*;

/**
 * @author yec
 * @description
 * @Data 2026/1/2
 */
public class SplitExtractionDownloader {

    private SplitExtractionDownloader() {}

    public static byte[] downloadResult(String applicationId) {
        return downloadResult(Idp.getRestClient(), applicationId);
    }

    public static File downloadResultToFile(String applicationId, String outputFilePath) throws IOException {
        return downloadResultToFile(Idp.getRestClient(), applicationId, outputFilePath);
    }

    private static byte[] downloadResult(final IdpRestClient client, String applicationId) {
        String url = Idp.getSplitExtractionDownloadUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, applicationId);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Split extraction download failed: Unable to connect to server");
        }

        if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }

            String errorMessage = restException.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("not done yet") ||
                    errorMessage.contains("Only status 100 supports downloading results")) {
                    throw new DownloadNotReadyException("Task is not ready for download. Current status must be 100 (Success).");
                }
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

    private static File downloadResultToFile(final IdpRestClient client, String applicationId, String outputFilePath)
        throws IOException {
        byte[] data = downloadResult(client, applicationId);

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

    public static class DownloadNotReadyException extends RuntimeException {
        public DownloadNotReadyException(String message) {
            super(message);
        }

        public DownloadNotReadyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static boolean isReadyForDownload(String applicationId) {
        try {
            IdpResponse<Integer> statusResponse = SplitExtractionStatusFetcher.fetchStatus(applicationId);
            if (statusResponse != null && statusResponse.isSuccessful() && statusResponse.getData() != null) {
                Integer statusCode = statusResponse.getData();
                return statusCode != null && statusCode == 100; // Success status code
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static byte[] downloadIfReady(String applicationId) {
        if (!isReadyForDownload(applicationId)) {
            throw new DownloadNotReadyException("Task is not ready for download. Status must be 100 (Success).");
        }
        return downloadResult(applicationId);
    }
}