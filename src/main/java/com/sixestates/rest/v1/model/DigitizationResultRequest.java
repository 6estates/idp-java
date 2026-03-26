package com.sixestates.rest.v1.model;

/**
 * Request model for digitization result download.
 */
public class DigitizationResultRequest {
    private String applicationId;
    private Integer type; // 1:Word, 2:Txt, 3:Json
    private Integer fontSize;

    public DigitizationResultRequest(String applicationId, Integer type) {
        this.applicationId = applicationId;
        this.type = type;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public void setFontSize(Integer fontSize) {
        this.fontSize = fontSize;
    }
}
