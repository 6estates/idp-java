package com.sixestates.example;


import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.rest.v1.ResultExtractor;
import com.sixestates.type.FileInfo;
import com.sixestates.type.ResultDTO;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import com.sixestates.utils.Lists;

import java.io.File;
import java.io.FileInputStream;

public class Example {

    public static final String TOKEN = "XXXXXXX";
    public static final String FILE_PATH = "/Users/6e/Downloads/CBKS.pdf" ;
    public static final String FILE_TYPE = "CBKS";

    /**
     * Example IDP usage.
     *
     * @param  args command line args
     * @throws Exception if unable to generate InterruptedException or ApiException
     */
    public static void main(String[] args) throws Exception {

        Idp.init(TOKEN);

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
        }catch (final ApiException | ApiConnectionException e) {
            System.err.println(e);
        }

        // Extract the result
        if(taskDto != null && taskDto.getStatus() == 200) {

            try {
                System.out.println(ResultExtractor.extractResultByTaskid(taskDto.getData()));
            }catch (ApiException e) {
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
        if(taskDto.getStatus() == 200) {
            try{
                boolean taskDone = false;
                while(!taskDone){
                    String taskId = taskDto.getData();
                    ResultDTO resultDto = ResultExtractor.extractResultByTaskid(taskId);

                    if(resultDto.getTaskStatus().equals("Done")) {
                        //Print the response json string
                        System.out.println(resultDto.getRespJson());
                        taskDone = true;
                    }else {
                        System.out.println("The status is Doing or Init, please request again after 30 seconds ");
                        Thread.sleep( 1000 * 30);
                    }
                }
            }catch(ApiException e ){
                System.err.println(e);
            }
        }

        // Submit task using InputStream
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);

            taskInfo = TaskInfo.builder()
                    .fileInfos(Lists.newArrayList(new FileInfo("acount_statement_mandiri.pdf", fis)))
                    .fileType("CBKS")
                    .build();

            taskDto = ExtractSubmitter.submit(taskInfo);
            System.out.println("taskId: " + taskDto.getData());
            fis.close();
        }catch(Exception e) {
            System.out.println(e);
        }finally {
            if(fis!=null) fis.close();
        }
    }
}
