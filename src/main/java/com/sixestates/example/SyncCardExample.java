package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.rest.v1.SyncCardExtractor;
import com.sixestates.type.IdpResponse;

import java.io.IOException;
import java.io.InputStream;

import static com.sixestates.example.Example.TOKEN;

/**
 * @author yec
 * @description
 * @Data 2026/1/2
 */
public class SyncCardExample {

    public static void main(String[] args) {
        Idp.init(TOKEN);
        test();
    }


    public static void test() {
        // 示例1：基本用法
        try (InputStream fileStream =  SyncCardExample.class.getClassLoader()
            .getResourceAsStream("files/sync_card.pdf")) {
            SyncCardExtractor.CardExtractionRequest request = new SyncCardExtractor.CardExtractionRequest(
                fileStream,
                "sync_card.pdf",
                "NPWP"  // 卡片类型：NPWP
            );

            IdpResponse<SyncCardExtractor.CardExtractionResult> response = SyncCardExtractor.extract(request);

            if (response.isSuccessful()) {
                SyncCardExtractor.CardExtractionResult result = response.getData();

                System.out.println("同步提取完成！");
                System.out.println("文件: " + result.getTaskFileName());
                System.out.println("卡片类型: " + result.getFileType());
                System.out.println("应用ID: " + result.getApplicationId());
                System.out.println("任务ID: " + result.getTaskId());
                System.out.println("\n关键信息:");
            }
        } catch (IOException e) {
            System.err.println("读取文件失败: " + e.getMessage());
        }
    }
}
