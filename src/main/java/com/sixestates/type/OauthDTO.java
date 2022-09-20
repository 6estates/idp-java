package com.sixestates.type;

import lombok.Data;

@Data
public class OauthDTO {
    private OauthDataDTO data;
    private int errorCode;
    private String message;
    private int status;
}