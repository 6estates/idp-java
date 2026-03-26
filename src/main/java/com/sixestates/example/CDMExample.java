package com.sixestates.example;


import com.sixestates.Idp;
import com.sixestates.http.Response;
import com.sixestates.rest.v1.crossdocmatch.CDMApi;
import com.sixestates.type.IdpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.sixestates.example.Example.TOKEN;

/**
 * Cross Document Matching (CDM) API Usage Example
 */
public class CDMExample {

    public static void main(String[] args) throws IOException {
        // 初始化 SDK
        Idp.init(TOKEN);

        // 1. 提交任务
//         String appId = testSubmit();

        // 2. 查询状态 (假设已知 ID)
        String applicationId = "CDM206615714050613410";
//        testGetStatus(applicationId);

        // 3. 下载结果
        testGetResult(applicationId);
    }

    /**
     * 8.1.1 提交跨单据匹配任务
     */
    public static String testSubmit() throws IOException {
        CDMApi.CDMSubmitRequest request = new CDMApi.CDMSubmitRequest();
        request.setMatchingGroupCode("CDM_GROUP_100");
        request.setHitl(false);
        request.setMergeFile(false);

        // 添加第一个文件 (指定 docType)
        try (InputStream stream1 = CDMExample.class.getClassLoader().getResourceAsStream("files/docagent.pdf")) {
            CDMApi.FileParam file1 = new CDMApi.FileParam(stream1, "docagent.pdf");
            file1.setDocType("CBKS");
            request.addFileParam(file1);

            // 添加第二个文件 (使用检测模式)
            try (InputStream stream2 = CDMExample.class.getClassLoader().getResourceAsStream("files/CBKS.pdf")) {
                CDMApi.FileParam file2 = new CDMApi.FileParam(stream2, "CBKS.pdf");
                file2.setDetectionMode(1);
                request.addFileParam(file2);

                IdpResponse<String> response = CDMApi.submit(request);

                if (response.isSuccessful()) {
                    String applicationId = response.getData();
                    System.out.println("CDM task submitted. Application ID: " + applicationId);
                    return applicationId;
                }
            }
        } catch (Exception e) {
            System.err.println("Submit failed: " + e.getMessage());
        }
        return null;
    }

    /**
     * 8.1.2 查询任务状态
     */
    public static void testGetStatus(String applicationId) {
        try {
            IdpResponse<Integer> response = CDMApi.queryStatus(applicationId);

            if (response.isSuccessful()) {
                Integer statusCode = response.getData();
                System.out.println("Current Task Status: " + statusCode);

                // 状态说明: 0:进行中, 100:完成, -10:失败
                if (statusCode == 100) {
                    System.out.println("Task finished! Ready to download.");
                } else if (statusCode < 0) {
                    System.out.println("Task failed with code: " + statusCode);
                } else {
                    System.out.println("Task is still processing...");
                }
            }
        } catch (Exception e) {
            System.err.println("Query status error: " + e.getMessage());
        }
    }

    /**
     * 8.1.3 & 8.1.4 下载匹配结果 (Excel 或 ZIP)
     */
    public static void testGetResult(String applicationId) {
        String homeDir = System.getProperty("user.home");
        // 根据需求切换文件名后缀
        boolean downloadZip = true;
        String fileName = downloadZip ? "cdm_result.zip" : "cdm_result.xlsx";
        String filePath = homeDir + "/Downloads/" + fileName;

        // 1. 获取 Response 对象
        Response response = CDMApi.downloadResult(applicationId, downloadZip);

        if (response != null && response.getStatusCode() == 200) {
            File resultFile = new File(filePath);

            // 2. 使用 try-with-resources 自动关闭从 response 获取的流
            try (InputStream is = response.getStream();
                 FileOutputStream fos = new FileOutputStream(resultFile)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }

                System.out.println("Result saved successfully to: " + resultFile.getAbsolutePath());
                System.out.println("File size: " + resultFile.length() + " bytes");

            } catch (IOException e) {
                System.err.println("File IO error while saving: " + e.getMessage());
            }
        } else {
            // 3. 处理失败情况，如果是报错信息，通常可以通过 getContent() 查看
            String errorMsg = (response != null) ? response.getContent() : "No response from server";
            System.err.println("Download failed. Status: " + (response != null ? response.getStatusCode() : "null"));
            System.err.println("Error details: " + errorMsg);
        }
    }
}