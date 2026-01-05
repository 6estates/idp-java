package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.enums.SplitAndExtStatusEnum;
import com.sixestates.rest.v1.splitext.SplitExtractionDownloader;
import com.sixestates.rest.v1.splitext.SplitExtractionStatusFetcher;
import com.sixestates.rest.v1.splitext.SplitExtractionSubmitter;
import com.sixestates.type.IdpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.sixestates.example.Example.TOKEN;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class SplitExtExample {

    public static void main(String[] args) throws IOException {
        Idp.init(TOKEN);
//        testSubmit();
//        testGetStatus();
        testGetResult();
    }

    public static void testGetResult() throws IOException {
        String applicationId = "SE192007850447357447"; // 假设的32位ID
        String homeDir = System.getProperty("user.home");
        String filePath = homeDir + "/Downloads/split_result.zip";

        try {
            byte[] resultData = SplitExtractionDownloader.downloadResult(applicationId);
            System.out.println("Downloaded " + resultData.length + " bytes");

            // 保存到文件
            File resultFile = new File(filePath);
            try (FileOutputStream fos = new FileOutputStream(resultFile)) {
                fos.write(resultData);
            }
            System.out.println("Result saved to: " + resultFile.getAbsolutePath());
        } catch (SplitExtractionDownloader.DownloadNotReadyException e) {
            System.out.println("Task not ready for download: " + e.getMessage());
            System.out.println("Current status must be 100 (Success)");
        } catch (IOException e) {
            System.err.println("Failed to save file: " + e.getMessage());
        }
    }

    public static void testGetStatus() {
        String applicationId = "SE195013301023812907";

// 示例1：使用枚举处理状态
        try {
            IdpResponse<Integer> response = SplitExtractionStatusFetcher.fetchStatus(applicationId);

            if (response.isSuccessful()) {
                Integer statusCode = response.getData();

                // 转换为枚举
                SplitAndExtStatusEnum statusEnum = SplitAndExtStatusEnum.convert(statusCode);

                System.out.println("Status: " + statusEnum.name());
                System.out.println("Message: " + statusEnum.getMsg());
                System.out.println("Code String: " + statusEnum.codeStr);

            }
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
        }
    }

    public static void testSubmit() throws IOException {
        // 示例1：基本用法
        Integer groupId = 1;
        try (InputStream pdfStream = SplitExtExample.class.getClassLoader().getResourceAsStream("files/CBKS.pdf")) {
            SplitExtractionSubmitter.SplitExtractionRequest request = new SplitExtractionSubmitter.SplitExtractionRequest(pdfStream,
                "CBKS.pdf", groupId  // 贸易单据组
            );

            IdpResponse<String> response = SplitExtractionSubmitter.submit(request);

            if (response.isSuccessful()) {
                String applicationId = response.getData();
                System.out.println("Split extraction task submitted. Application ID: " + applicationId);
                System.out.println("Application ID length: " + applicationId.length()); // 应至少32位
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
    }
}
