package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.http.Response;
import com.sixestates.rest.v1.fsagent.FsAgentApi;
import com.sixestates.type.IdpResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.sixestates.example.Example.TOKEN;

public class FsAgentExample {

    public static void main(String[] args) throws IOException {
        Idp.init(TOKEN);

        // 1. 提交任务
        // String appId = testSubmit();

        // 2. 查询详细状态
        String applicationId = "FSA12345678901234567890123456789012";
        testQueryStatus(applicationId);

        // 3. 导出结果
        testExport(applicationId);
    }

    public static String testSubmit() throws IOException {
        try (InputStream is = FsAgentExample.class.getClassLoader().getResourceAsStream("files/bank_statement.pdf")) {
            FsAgentApi.FsAgentRequest request = new FsAgentApi.FsAgentRequest(is, "bank_statement.pdf");
            request.setHitl(true); // 开启 HITL

            IdpResponse<String> response = FsAgentApi.submit(request);
            if (response.isSuccessful()) {
                System.out.println("FS Agent Task Submitted: " + response.getData());
                return response.getData();
            }
        }
        return null;
    }

    public static void testQueryStatus(String applicationId) {
        IdpResponse<FsAgentApi.FsAgentStatus> response = FsAgentApi.queryStatus(applicationId);
        if (response.isSuccessful()) {
            FsAgentApi.FsAgentStatus data = response.getData();
            System.out.println("Status Name: " + data.getStatus());
            System.out.println("Status Code: " + data.getStatusCode());

            if (data.getStatusCode() == 11) {
                System.out.println("Task complete, ready to export.");
            } else if (data.getStatusCode() == -1) {
                System.err.println("Task failed: " + data.getErrorMsg());
            }
        }
    }

    public static void testExport(String applicationId) {
        String savePath = System.getProperty("user.home") + "/Downloads/fs_agent_result.xlsx";

        try {
            Response response = FsAgentApi.downloadResult(applicationId);
            File file = new File(savePath);
            try (InputStream is = response.getStream();
                 FileOutputStream fos = new FileOutputStream(file)) {

                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                System.out.println("FS Agent result saved: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Export error: " + e.getMessage());
        }
    }
}