package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.http.Response;
import com.sixestates.rest.v1.digitization.DigitizationApi;
import com.sixestates.type.IdpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.sixestates.example.Example.TOKEN;

public class DigitizationExample {

    public static void main(String[] args) throws IOException {
        Idp.init(TOKEN);

        // 1. 提交数字化任务
//         String appId = testSubmit();

        // 2. 查询状态
        String applicationId = "DIG206610920363794594";
//        testStatus(applicationId);

        // 3. 下载 Word 结果
        testDownloadWord(applicationId);
    }

    public static String testSubmit() throws IOException {
        try (InputStream is = DigitizationExample.class.getClassLoader().getResourceAsStream("files/CBKS.pdf")) {
            IdpResponse<String> response = DigitizationApi.submit(is, "CBKS.pdf");
            if (response.isSuccessful()) {
                System.out.println("Submitted! AppID: " + response.getData());
                return response.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void testStatus(String applicationId) {
        IdpResponse<Integer> response = DigitizationApi.queryStatus(applicationId);
        if (response.isSuccessful()) {
            Integer status = response.getData();
            // 0(Init), 10(On Process), 100(Finished), -10(Failed)
            System.out.println("Digitization status: " + status);
        }
    }

    public static void testDownloadWord(String applicationId) {
        String savePath = System.getProperty("user.home") + "/Downloads/ocr_result.docx";

        // 构造请求：Word类型(1)，字号10
        DigitizationApi.DigitizationResultRequest requestParamer =
            new DigitizationApi.DigitizationResultRequest(applicationId, 1);
        requestParamer.setFontSize(10);

        try {
            Response response = DigitizationApi.downloadResult(requestParamer);
            File file = new File(savePath);
            try (InputStream is = response.getStream();
                 FileOutputStream fos = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }
                System.out.println("Word file saved to: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Download failed: " + e.getMessage());
        }
    }
}