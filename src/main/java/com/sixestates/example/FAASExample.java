package com.sixestates.example;


import com.sixestates.Idp;
import com.sixestates.exception.ApiConnectionException;
import com.sixestates.exception.ApiException;
import com.sixestates.rest.v1.FAASApi;
import com.sixestates.rest.v1.ResultExtractor;
import com.sixestates.type.FAASTaskInfo;
import com.sixestates.type.FAASTaskStatus;
import com.sixestates.type.TaskDTO;
import com.sixestates.utils.Lists;

import java.io.File;

public class FAASExample {

    public static final String TOKEN = "XXXXXXX";
    public static final String FILE_PATH = "/Users/6e/Downloads/CBKS.pdf" ;

    /**
     * Example FAAS usage.
     *
     * @param  args command line args
     * @throws Exception if unable to generate InterruptedException or ApiException
     */
    public static void main(String[] args) throws Exception {

        Idp.init(TOKEN);

        // Submit a task
        TaskDTO taskDto = null;
        try {
            FAASTaskInfo taskInfo = FAASTaskInfo.builder()
                    .files(Lists.newArrayList(new File(FILE_PATH)))
                    .customerType("2")
                    .informationType(0)
                    .build();
            taskDto = FAASApi.submitFAASTask(taskInfo);

            System.out.println("taskId: " + taskDto.getData());
            FAASTaskStatus taskStatus = FAASApi.getTaskStatus("");
            System.out.println(taskStatus);
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

    }
}
