package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.rest.v1.ResultExtractor;
import com.sixestates.type.*;
import com.sixestates.utils.Lists;
import com.sixestates.utils.OauthUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class OauthExample {
    public static final String AUTHORIZATION = "XXXXXXX";
    public static final String FILE_NAME = "acount_statement_mandiri.pdf";
    public static final String FILE_PATH = "/home/Documents/acount_statement_mandiri.pdf" ;
    public static final String FILE_TYPE = "CBKS";

    /**
     * Example IDP usage.
     *
     * @param  args command line args
     * @throws Exception if unable to generate InterruptedException or ApiException
     */
    public static void main(String[] args) throws Exception {

        OauthDTO oauthDTO = OauthUtils.getIDPAuthorization("xxxxxx" , "xxxxxx");
        String authorization = oauthDTO.getData().getValue();
        Idp.initAuthorization(authorization);
        System.out.println("The AUTHORIZATION will expire in " + oauthDTO.getData().getExpiration() + " seconds");
        // Submit a task
        TaskDTO taskDto = null;
        try {
            TaskInfo taskInfo = TaskInfo.builder()
                    .files(Lists.newArrayList(new File(FILE_PATH)))
                    .fileType(FILE_TYPE)
                    .hitl(true)
                    .build();
            taskDto = ExtractSubmitter.submit(taskInfo);

            System.out.println("taskId: " + taskDto.getData());
        } catch (final ApiException | ApiConnectionException e) {
            System.err.println(e);
        }

        // Extract the result
        if (taskDto != null && taskDto.getStatus() == 200) {

            try {
                System.out.println(ResultExtractor.extractResultByTaskid(taskDto.getData()));
            } catch (ApiException e) {
                System.err.println(e);
            }
        }


        // Submit the  new task
        TaskInfo taskInfo = TaskInfo.builder()
                .files(Lists.newArrayList(new File("/home/Documents/1006027_doc_MutasiBank_Bulan_2-1646623361478.jpg")))
                .fileType("CBKS")
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo);
        System.out.println("taskId: " + taskDto.getData());

        // Extract the result
        if (taskDto.getStatus() == 200) {
            try {
                boolean taskDone = false;
                while (!taskDone) {
                    String taskId = taskDto.getData();
                    ResultDTO resultDto = ResultExtractor.extractResultByTaskid(taskId);

                    if (resultDto.getTaskStatus().equals("Done")) {
                        //Print the response json string
                        System.out.println(resultDto.getRespJson());
                        taskDone = true;
                    } else {
                        System.out.println("The status is Doing or Init, please request again after 30 seconds ");
                        Thread.sleep(1000 * 30);
                    }
                }
            } catch (ApiException e) {
                System.err.println(e);
            }
        }

        // Submit task using InputStream

        Map<String, InputStream> inputStreamMap = new HashMap<>();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);
            inputStreamMap.put("acount_statement_mandiri.pdf", fis);
            taskInfo = TaskInfo.builder()
                    .fileInfos(Lists.newArrayList(new FileInfo("acount_statement_mandiri.pdf", fis)))
                    .fileType("CBKS")
                    .build();

            taskDto = ExtractSubmitter.submit(taskInfo);
            System.out.println("taskId: " + taskDto.getData());
            fis.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            if (fis != null) fis.close();
        }
    }
}
