package com.sixestates.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class FileStoreUtils {
    public static void localFileSystemStore(String path, String fileName, byte[] fileBytes) {
        Date d1 = new Date();
        File file = new File(path + fileName);
        System.out.println("write file: " + fileName );
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = null;
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(fileBytes);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
