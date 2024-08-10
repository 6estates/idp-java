package com.sixestates.type;

import java.io.InputStream;

/**
 * @author kechen, 09/08/24.
 */
public class FileInfo {
    private String fileName;

    private InputStream inputStream;

    public FileInfo() {
    }

    public FileInfo(String fileName, InputStream inputStream) {
        this.fileName = fileName;
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }
}
