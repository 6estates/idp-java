package com.sixestates.example;

import com.alibaba.fastjson.JSON;
import com.sixestates.Idp;
import com.sixestates.rest.v1.*;
import com.sixestates.type.IdpResponse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static com.sixestates.example.Example.TOKEN;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class FaasExample {

    public static void main(String[] args) throws FileNotFoundException {
        Idp.init(TOKEN);
//        testSubmit();
        testGetStatus();
//        testExport();
//        testGetResult();
//        testAddAdditionalFile();
    }

    public static void testSubmit() throws FileNotFoundException {
        // 示例1：简单用法
//        InputStream zipInputStream = ExtractHistoryQuerier.class.getClassLoader()
//            .getResourceAsStream("files/CBKS.pdf");
//        FaasAnalysisSubmitter.FaasAnalysisRequest request = new FaasAnalysisSubmitter.FaasAnalysisRequest(
//            zipInputStream,
//            "CBKS.pdf",
//            "2", // customerType: 2 for Company/Business
//            1    // informationType: 1 for Existing Customer
//        );
//        request.setCifNumber("12345");
//        request.setApplicationNumber("APP-001");
//        request.setCurrency("SGD");
//        request.setHitl("false");
//
//        IdpResponse<String> response = FaasAnalysisSubmitter.submit(request);
//        if (response.isSuccessful()) {
//            String taskId = response.getData(); // e.g., "FAAS123456789"
//            System.out.println("Analysis task created: " + taskId);
//        }

        // 示例2：使用Builder模式构建复杂请求
        List<FaasAnalysisSubmitter.RelatedParty> relatedParties = new ArrayList<>();
        FaasAnalysisSubmitter.RelatedParty relatedParty = new FaasAnalysisSubmitter.RelatedParty();
        relatedParty.setRelatedType("ORG_TYPE");
        relatedParty.setOrgType(101);
        relatedParty.setName("ABC Company");
        relatedParties.add(relatedParty);

        List<FaasAnalysisSubmitter.SupplierBuyer> suppliers = new ArrayList<>();
        FaasAnalysisSubmitter.SupplierBuyer supplier = new FaasAnalysisSubmitter.SupplierBuyer();
        supplier.setBusinessRelationship(2);
        supplier.setLongRelationship(3);
        supplier.setCompanyName("Supplier XYZ");
        suppliers.add(supplier);
        InputStream zipInputStream2 = ExtractHistoryQuerier.class.getClassLoader()
            .getResourceAsStream("files/document.zip");

        FaasAnalysisSubmitter.FaasAnalysisRequest request2 = new FaasAnalysisSubmitter.RequestBuilder(
            zipInputStream2, "document.zip", "2", 1)
            .withCifNumber("12345")
            .withApplicationNumber("APP-001")
            .withCurrency("SGD")
            .withLoanAmount(100000.0f)
            .withHitl("false")
            .withAutomatic(true)
            .withRelatedPartiesJson(JSON.toJSONString(relatedParties))
            .withSupplierBuyerJson(JSON.toJSONString(suppliers))
            .build();

        IdpResponse<String> response2 = FaasAnalysisSubmitter.submit(request2);
        if (response2.isSuccessful()) {
            String taskId = response2.getData(); // e.g., "FAAS123456789"
            System.out.println("Analysis task created: " + taskId);
        }
    }

    public static void testGetStatus() {
        // 示例1：简单查询状态
        String applicationId = "FAAS194990067029126443";
        IdpResponse<FaasAnalysisStatusFetcher.FaasAnalysisStatusData> response =
            FaasAnalysisStatusFetcher.fetchStatus(applicationId);

        if (response.isSuccessful()) {
            FaasAnalysisStatusFetcher.FaasAnalysisStatusData statusData = response.getData();
            System.out.println("Status: " + statusData.getAnalysisStatus());
            System.out.println("Status Code: " + statusData.getAnalysisStatusCode());

            if (statusData.isDone()) {
                System.out.println("Analysis completed successfully!");
                // 现在可以获取分析结果
            } else if (statusData.isFailed()) {
                System.out.println("Analysis failed: " + statusData.getAnalysisErrorMsg());
            } else {
                System.out.println("Analysis is still in progress...");
            }
        }
    }

    public static void testExport() {
        String applicationId = "FAAS194990067029126443";

// 示例1：直接导出（如果分析未完成会抛出异常）
        try {
            byte[] resultData = FaasAnalysisExporter.exportResult(applicationId);
            System.out.println("Exported " + resultData.length + " bytes");
        } catch (FaasAnalysisExporter.AnalysisNotReadyException e) {
            System.out.println("Analysis not ready yet: " + e.getMessage());
        }

// 示例2：导出到指定文件
        String homeDir = System.getProperty("user.home");
        String filePath = homeDir + "/Downloads/analysis_result.zip";

        try {
            File resultFile = FaasAnalysisExporter.exportResultToFile(
                applicationId,
                filePath
            );
            System.out.println("Result saved to: " + resultFile.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Failed to save file: " + e.getMessage());
        }
    }

    public static void testGetResult() {
        String applicationId = "FAAS194990067029126443";

// 示例1：直接获取结果（如果分析未完成会抛出异常）
        try {
            IdpResponse<Object> response = FaasAnalysisResultFetcher.fetchResult(applicationId);

            if (response.isSuccessful()) {
                Object resultData = response.getData();
                System.out.println("Result received: " + JSON.toJSONString(resultData));

            }
        } catch (FaasAnalysisResultFetcher.AnalysisNotReadyException e) {
            System.out.println("Analysis not ready yet: " + e.getMessage());
        }
    }

    public static void testAddAdditionalFile() {
        String applicationId = "FAAS194990067029126443";

// 示例1：简单添加文件
        try (InputStream fileStream = FaasExample.class.getClassLoader().getResourceAsStream("files/CBKS.pdf")) {
            FaasAnalysisAdder.FileAdditionRequest request = new FaasAnalysisAdder.FileAdditionRequest(
                applicationId,
                fileStream,
                "CBKS.pdf"
            );

            IdpResponse<String> response = FaasAnalysisAdder.addFiles(request);

            if (response.isSuccessful()) {
                String updatedAppId = response.getData();
                System.out.println("Files added successfully to application: " + updatedAppId);
            }
        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        }
    }

}
