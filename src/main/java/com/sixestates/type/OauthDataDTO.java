package com.sixestates.type;

import lombok.Data;

@Data
public class OauthDataDTO {
    private Object additionalInformation;
    private String  expiration;
    private boolean expired;
    private int expiresIn;
    private String value;
}