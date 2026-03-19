package com.sixestates.example;


import com.sixestates.Idp;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import com.sixestates.utils.Lists;

import java.io.File;

public class Example {

    public static final String TOKEN = "XXXXXXX";
    public static final String FILE_PATH = "/Users/yecong/Downloads/decrypted.pdf";

    /**
     * Example IDP usage.
     *
     * @param  args command line args
     * @throws Exception if unable to generate InterruptedException or ApiException
     */
    public static void main(String[] args) throws Exception {
        Idp.init(TOKEN);
//        // Submit a task
        TaskDTO taskDto = null;
        try {
            TaskInfo taskInfo = TaskInfo.builder()
                .files(Lists.newArrayList(new File(FILE_PATH)))
                    .fileType("CBKS")
                    .build();

            taskDto = ExtractSubmitter.submit(taskInfo);
            System.out.println("taskId: " + taskDto.getData());
        }catch(Exception e) {
            System.out.println(e);
        }
    }
}
