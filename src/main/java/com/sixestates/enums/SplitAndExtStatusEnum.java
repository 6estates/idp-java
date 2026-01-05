package com.sixestates.enums;


import com.sixestates.exception.ParameterIllegalException;

/**
 * @author yec
 * @description
 * @Data 2022/2/14
 */
public enum SplitAndExtStatusEnum {
    Init(0, "Init", "Init"),
    Running(10, "Extracting", "Extracting"),
    Hilt(13, "HITL", "HITL"),
    Success(100, "Extraction Done", "Extraction Done"),
    Extraction_Failed(-10, "Extraction Failed", "Extraction Failed"),
    Invalid(-20, "Invalid", "Invalid"),
    Unreadable(-21, "Unreadable", "Unreadable");


    public int code;
    public String msg;
    public String codeStr;

    SplitAndExtStatusEnum(Integer code, String msg, String codeStr) {
        this.code = code;
        this.msg = msg;
        this.codeStr = codeStr;
    }

    public static SplitAndExtStatusEnum convert(int code) {
        for (SplitAndExtStatusEnum status : SplitAndExtStatusEnum.values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new ParameterIllegalException(String.format("status value %s is not exist", code));
    }

    public String getMsg() {
        return msg;
    }

    public Integer getValue() {
        return code;
    }
}