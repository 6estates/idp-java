package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.rest.v1.ExtractHistoryQuerier;
import com.sixestates.rest.v1.TaskToHitlConverter;
import com.sixestates.type.IdpResponse;
import com.sixestates.type.faas.HistoryQueryParams;
import com.sixestates.type.faas.HistoryQueryParamsBuilder;

import java.util.List;

import static com.sixestates.example.Example.TOKEN;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class ExtHistoryExample {

    public static void main(String[] args) {
        Idp.init(TOKEN);
//        testQuery();

        testAddToHitl();
    }

    private static void testQuery() {
        // 使用Builder模式构建查询参数
        HistoryQueryParams params = HistoryQueryParamsBuilder.aHistoryQueryParams()
            .withPage(1)
            .withLimit(20)
            .withSortOrder("descending")
            .withSortColumn("create_time")
            .withStatus(2)
            .withFileTypeCode("cbks")
            .withSource(2) // API来源
            .withHitl(true)
            .withStartCreateTime("2025-10-01")
            .withEndCreateTime("2025-12-31")
            .build();

        // 执行查询
        ExtractHistoryQuerier.HistoryTaskListResponse listResponse = ExtractHistoryQuerier.query(params);

        if (listResponse.isSuccessful()) {
            List<ExtractHistoryQuerier.HistoryTaskDTO> tasks = listResponse.getData().getResult();
            int total = listResponse.getData().getTotal();

            for (ExtractHistoryQuerier.HistoryTaskDTO task : tasks) {
                System.out.println("Task ID: " + task.getId());
                System.out.println("File Name: " + task.getFileName());
                System.out.println("Created: " + task.getCreateTime());
            }
        }
    }

    private static void testAddToHitl() {
        // 方法2：使用Builder模式
        TaskToHitlConverter.HitlConversionRequest request2 = new TaskToHitlConverter.HitlConversionRequest();
        request2.setApplicationId("195020863723023659");
        request2.setCallback("https://callback.example.com");
        request2.setCallbackMode(1);

        // 方法3：简化调用
        IdpResponse<TaskToHitlConverter.HitlConversionData> response3 = TaskToHitlConverter.convert(request2);

        // 检查结果
        if (response3.isSuccessful()) {
            System.out.println("API调用成功");
            if (response3.getData().getSuccess().equals(true)) {
                System.out.println("任务已成功转为HITL");
            } else {
                System.out.println("转为HITL失败: " + response3.getData().getMessage());
            }
        }
    }


}
