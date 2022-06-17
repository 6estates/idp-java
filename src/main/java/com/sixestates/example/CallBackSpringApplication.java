package com.sixestates.example;
/*
import com.sixestates.utils.FileStoreUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;

@SpringBootApplication
public class CallBackSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallBackSpringApplication.class, args);
    }

    @Controller
    @RequestMapping("/")
    public class CallBackController {
        @RequestMapping(value = "/", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
        @ResponseBody
        public ResponseEntity mode0or1CallBack(@RequestBody String jsonStr) {
            System.out.println("Receive a callback request!");
            System.out.println("Callback body: " + jsonStr);
            return ResponseEntity.ok(200);
        }

        @RequestMapping(value = "/mode2")
        @ResponseBody
        public ResponseEntity mode2CallBack(@RequestParam("file") MultipartFile file, @RequestParam("result") MultipartFile json) {
            try {
                System.out.println("Receive a callback request!");
                // Receive the result json file
                String jsonStr = new String(json.getBytes());
                System.out.println("Callback body: " + jsonStr);

                // Receive the file bytes
                //FileStoreUtils.localFileSystemStore("/home/Documents/", file.getOriginalFilename(), file.getBytes());
                System.out.println("Callback fileName: " + file.getOriginalFilename());
                Bytes[] fileBytes = file.getBytes());
                return ResponseEntity.ok(200);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.ok(500);
                }
        }
    }
}
*/
