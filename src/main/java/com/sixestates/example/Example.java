package com.sixestates.example;


import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.rest.v1.ResultExtractor;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import java.io.FileInputStream;

public class Example {

    public static final String TOKEN = "XXXXXXX";
    public static final String FILE_NAME = "acount_statement_mandiri.pdf";
    public static final String FILE_PATH = "/home//lay/Documents/acount_statement_mandiri.pdf" ;
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
                    .fileName(FILE_NAME)
                    .filePath(FILE_PATH)
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
                .fileName("1006027_doc_MutasiBank_Bulan_2-1646623361478.jpg")
                .filePath("/home/Documents/1006027_doc_MutasiBank_Bulan_2-1646623361478.jpg")
                .fileType("CBKS")
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo);
        System.out.println("taskId: " + taskDto.getData());

        // Extract the result
        if( taskDto.getStatus() == 200) {
            try {
                String respJson = ResultExtractor.extractResultByTaskid(taskDto.getData());
                System.out.println(respJson);
            }catch (ApiException e){
                System.err.println(e);
            }

            // Wait until the task done
            Thread.sleep( 1000 * 60 * 3 );
            String respJson = ResultExtractor.extractResultByTaskid(taskDto.getData());
            //Print the response json string
            System.out.println(respJson);
        }

        // Submit task using InputStream
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(FILE_PATH);

            taskInfo = TaskInfo.builder()
                    .fileName("acount_statement_mandiri.pdf")
                    .inputStream(fis)
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
