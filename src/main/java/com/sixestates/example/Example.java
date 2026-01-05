package com.sixestates.example;


import com.sixestates.Idp;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;

import java.io.InputStream;

public class Example {

//        public static final String TOKEN = "XXXXXXX";
    public static final String TOKEN = "VIfKfKB/U35E2/xv/ovJbONgONqbx8EugMGcYh3gT7DpL7qM2+lUKDP3e9hNdQpL";
    public static final String FILE_PATH = "files/CBKS.pdf";

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
        // Submit task using InputStream
        InputStream fis = null;
        try {
            fis = Example.class.getClassLoader()
                .getResourceAsStream(FILE_PATH);

            TaskInfo taskInfo = TaskInfo.builder()
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
            if(fis!=null) {
                fis.close();
            }
        }
    }
}
