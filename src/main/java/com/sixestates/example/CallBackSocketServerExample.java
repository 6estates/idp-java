package com.sixestates.example;

import com.sixestates.Idp;
import com.sixestates.rest.v1.CallBackSocketServer;
import com.sixestates.rest.v1.ExtractSubmitter;
import com.sixestates.type.TaskDTO;
import com.sixestates.type.TaskInfo;
import com.sixestates.utils.Lists;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class CallBackSocketServerExample {
    public static final String TOKEN = "XXXXXXX";
    public static final String FILE_NAME = "acount_statement_mandiri.pdf";
    public static final String FILE_PATH = "/home/Documents/acount_statement_mandiri.pdf" ;
    public static final String FILE_TYPE = "CBKS";
    public static final String CALLBACK_URL = "http://xxx.com";

    public static void main(String[] args) throws Exception {
        Idp.init(TOKEN);

        // Init a CallBackServer
        CallBackSocketServer callBackServer = new CallBackSocketServer("localhost", 8080);

        // Start the server asynchronously
        callBackServer.asynStartServer();

        // Submit tasks
        TaskDTO taskDto = null;

        TaskInfo taskInfo1 = TaskInfo.builder()
                .files(Lists.newArrayList(new File(FILE_PATH)))
                .fileType(FILE_TYPE)
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo1);
        System.out.println("taskId: " + taskDto.getData());

        TaskInfo taskInfo2 = TaskInfo.builder()
                .files(Lists.newArrayList(new File(FILE_PATH)))
                .fileType(FILE_TYPE)
                .callback(CALLBACK_URL)
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo2);
        System.out.println("taskId: " + taskDto.getData());

        TaskInfo taskInfo3 = TaskInfo.builder()
                .files(Lists.newArrayList(new File(FILE_PATH)))
                .fileType(FILE_TYPE)
                .callback(CALLBACK_URL)
                .callbackMode(0)
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo3);
        System.out.println("taskId: " + taskDto.getData());

        TaskInfo taskInfo4 = TaskInfo.builder()
                .files(Lists.newArrayList(new File(FILE_PATH)))
                .fileType(FILE_TYPE)
                .callback(CALLBACK_URL)
                .callbackMode(1)
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo4);
        System.out.println("taskId: " + taskDto.getData());

        TaskInfo taskInfo5 = TaskInfo.builder()
                .files(Lists.newArrayList(new File(FILE_PATH)))
                .fileType(FILE_TYPE)
                .callback(CALLBACK_URL)
                .callbackMode(2)
                .build();
        taskDto = ExtractSubmitter.submit(taskInfo5);
        System.out.println("taskId: " + taskDto.getData());

        System.out.println("Wait callback request");
        Thread.sleep( 1000 * 60 * 5 );
        ConcurrentHashMap<String, String> jsonMap = callBackServer.getJsonStrMap();
        for (String taskId: jsonMap.keySet()) {
            System.out.println(taskId  + ": " + jsonMap.get(taskId));
        }

        ConcurrentHashMap<String, byte[]> fileBytesMap = callBackServer.getFileBytesMap();
        for (String fileName: fileBytesMap.keySet()) {
            System.out.println(fileName + ": " + fileBytesMap.get(fileName).length);
        }

        // Stop the server
        callBackServer.stopServer();
    }
}
