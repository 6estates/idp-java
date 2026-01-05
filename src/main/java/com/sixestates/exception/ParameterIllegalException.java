package com.sixestates.exception;

/**
 * @author yec
 * @description
 * @Data 2025/12/30
 */
public class ParameterIllegalException extends IdpException {
    public ParameterIllegalException() {
        super("Invalid Parameter");
    }

    public ParameterIllegalException(Exception cause) {
        super("Invalid Parameter", cause);
    }

    public ParameterIllegalException(String msg) {
        super("Invalid Parameter " + msg);
    }

    public ParameterIllegalException(String msg, Exception cause) {
        super("Invalid Parameter " + msg, cause);
    }
}
