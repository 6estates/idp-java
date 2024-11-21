package com.sixestates.type;

/**
 * @author kechen, 30/10/24.
 */
public class FAASTaskStatus {

    private String applicationId;

    private String analysisStatus;

    private String analysisErrorMsg;

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getAnalysisStatus() {
        return analysisStatus;
    }

    public void setAnalysisStatus(String analysisStatus) {
        this.analysisStatus = analysisStatus;
    }

    public String getAnalysisErrorMsg() {
        return analysisErrorMsg;
    }

    public void setAnalysisErrorMsg(String analysisErrorMsg) {
        this.analysisErrorMsg = analysisErrorMsg;
    }
}
