package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.rest.v1.docAgent.DocumentAgentExporter;
import com.sixestates.rest.v1.docAgent.DocumentAgentStatusFetcher;
import com.sixestates.rest.v1.docAgent.DocumentAgentSubmitter;
import com.sixestates.type.IdpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.sixestates.example.Example.TOKEN;

/**
 * @author yec
 * @description
 * @Data 2026/1/2
 */
public class DocAgentExample {

    public static void main(String[] args) {
        Idp.init(TOKEN);
//        testSubmit();
        testGetStatus();
//        testGetResult();
    }

    public static void testGetResult() {
        String applicationId = "DAG195014481082843917";
        String homeDir = System.getProperty("user.home");
        String filePath = homeDir + "/Downloads/docagent_result.zip";

        try {
            byte[] resultData = DocumentAgentExporter.exportResult(applicationId);
            System.out.println("Exported " + resultData.length + " bytes");

            // 保存到文件
            File resultFile = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(resultFile)) {
                fos.write(resultData);
            }
            System.out.println("Result saved to: " + resultFile.getAbsolutePath());
        } catch (DocumentAgentExporter.ExportNotReadyException e) {
            System.out.println("Task not ready for export: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Failed to save file: " + e.getMessage());
        }
    }

    public static void testSubmit() {
        // 示例1：基本用法
        try (InputStream fileStream = DocAgentExample.class.getClassLoader().getResourceAsStream("files/docagent.pdf")) {
            DocumentAgentSubmitter.DocumentAgentRequest request = new DocumentAgentSubmitter.DocumentAgentRequest(
                "DAG24",  // 流程代码
                fileStream,
                "docagent.pdf"
            );

            IdpResponse<String> response = DocumentAgentSubmitter.submit(request);

            if (response.isSuccessful()) {
                String applicationId = response.getData();
                System.out.println("Document agent task submitted. Application ID: " + applicationId);

                // 验证ID长度
                if (DocumentAgentSubmitter.isValidForDatabaseStorage(applicationId)) {
                    System.out.println("Application ID is suitable for database storage");
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }

    }

    public static void testGetStatus() {
        String applicationId = "DAG195014481082843917"; // 假设的32位ID

// 示例1：基本状态查询
        try {
            IdpResponse<DocumentAgentStatusFetcher.DocAgentStatusData> response =
                DocumentAgentStatusFetcher.fetchStatus(applicationId);

            if (response.isSuccessful()) {
                DocumentAgentStatusFetcher.DocAgentStatusData statusData = response.getData();

                System.out.println("任务状态: " + statusData.getStatus());
                System.out.println("状态码: " + statusData.getStatusCode());
                System.out.println("状态枚举: " + statusData.getStatusEnum().getName());
                System.out.println("文件名: " + statusData.getFileName());
                System.out.println("任务流程: " + statusData.getTaskFlowName());
                System.out.println("上传用户: " + statusData.getUploadUserEmail());
                if (statusData.isFinished()) {

                    if (statusData.hasErrorTasks()) {
                        System.out.println("警告: 流程中有 " + statusData.getErrorTaskCount() + " 个错误任务");
                    }

                    System.out.println("任务已完成，可以下载结果文件");
                } else if (statusData.isFailed()) {
                    System.out.println("任务失败!");
                    if (statusData.getErrorMsg() != null) {
                        System.out.println("错误信息: " + statusData.getErrorMsg());
                    }
                    System.out.println("需要人工干预排查问题");
                } else if (statusData.isInProcess()) {
                    System.out.println("任务处理中，需要等待30秒后再次查询");
                }
            }
        } catch (Exception e) {
            System.err.println("查询状态失败: " + e.getMessage());
        }
    }


}
