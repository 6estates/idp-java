package com.sixestates.rest.v1.docAgent;

import com.alibaba.fastjson.JSON;
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

import java.io.*;
import java.util.List;

/**
 * @author yec
 * @description
 * @Data 2026/1/2
 */
public class DocumentAgentExporter {

    private DocumentAgentExporter() {
    }

    public static byte[] exportResult(String applicationId) {
        return exportResult(applicationId, null);
    }

    public static byte[] exportResult(String applicationId, List<String> taskCodes) {
        return exportResult(Idp.getRestClient(), applicationId, taskCodes);
    }

    public static File exportResultToFile(String applicationId, String outputFilePath) throws IOException {
        return exportResultToFile(applicationId, outputFilePath, null);
    }

    public static File exportResultToFile(String applicationId, String outputFilePath, List<String> taskCodes)
        throws IOException {
        return exportResultToFile(Idp.getRestClient(), applicationId, outputFilePath, taskCodes);
    }

    private static byte[] exportResult(final IdpRestClient client, String applicationId, List<String> taskCodes) {
        String url = Idp.getDocumentAgentExportUrl();

        Request request = new Request(
            HttpMethod.POST,
            url
        );
        request.setIsSubmit(false);
        addHeaderParams(request);
        addPostParams(request, applicationId, taskCodes);

        Response response = client.request(request);

        if (response == null) {
            throw new ApiConnectionException("Document agent export failed: Unable to connect to server");
        }

        if (!IdpRestClient.SUCCESS.test(response.getStatusCode())) {
            RestException restException = RestException.fromJson(response.getStream(), client.getObjectMapper());
            if (restException == null) {
                throw new ApiException("Server Error, no content");
            }

            String errorMessage = restException.getMessage();
            if (errorMessage != null) {
                if (errorMessage.contains("not ready") ||
                    errorMessage.contains("Only status 11 supports downloading results")) {
                    throw new ExportNotReadyException("Task is not ready for export. Current status must be 11 (Finished).");
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

    private static File exportResultToFile(final IdpRestClient client, String applicationId,
                                           String outputFilePath, List<String> taskCodes)
        throws IOException {
        byte[] data = exportResult(client, applicationId, taskCodes);

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

    private static void addPostParams(final Request request, String applicationId, List<String> taskCodes) {
        request.addPostParam("applicationId", applicationId);
        if (taskCodes != null && !taskCodes.isEmpty()) {
            request.addPostParam("taskCodes", JSON.toJSONString(taskCodes));
        }
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

    public static class ExportNotReadyException extends RuntimeException {
        public ExportNotReadyException(String message) {
            super(message);
        }

        public ExportNotReadyException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static boolean isReadyForExport(String applicationId) {
        try {
            IdpResponse<DocumentAgentStatusFetcher.DocAgentStatusData> statusResponse =
                DocumentAgentStatusFetcher.fetchStatus(applicationId);
            if (statusResponse != null && statusResponse.isSuccessful() && statusResponse.getData() != null) {
                DocumentAgentStatusFetcher.DocAgentStatusData statusData = statusResponse.getData();
                return DocAgentStatusForApiEnum.FINISHED.equals(statusData.getStatusEnum());
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static byte[] exportIfReady(String applicationId) {
        return exportIfReady(applicationId, null);
    }

    public static byte[] exportIfReady(String applicationId, List<String> taskCodes) {
        if (!isReadyForExport(applicationId)) {
            throw new ExportNotReadyException("Task is not ready for export. Status must be 11 (Finished).");
        }
        return exportResult(applicationId, taskCodes);
    }
}