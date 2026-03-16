package com.sixestates.type;

/**
 * @author kechen, 11/10/24.
 */
public class DocAgentStatusInfo {
    private Integer errorTaskCount;
    private String fileName;
    private Long finishTime;
    private String uploadUserEmail;
    private String taskFlowName;
    private Long uploadTime;
    private String status;

    public Integer getErrorTaskCount() {
        return errorTaskCount;
    }

    public void setErrorTaskCount(Integer errorTaskCount) {
        this.errorTaskCount = errorTaskCount;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public Long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Long finishTime) {
        this.finishTime = finishTime;
    }

    public String getUploadUserEmail() {
        return uploadUserEmail;
    }

    public void setUploadUserEmail(String uploadUserEmail) {
        this.uploadUserEmail = uploadUserEmail;
    }

    public String getTaskFlowName() {
        return taskFlowName;
    }

    public void setTaskFlowName(String taskFlowName) {
        this.taskFlowName = taskFlowName;
    }

    public Long getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Long uploadTime) {
        this.uploadTime = uploadTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
