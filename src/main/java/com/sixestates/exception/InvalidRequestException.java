package com.sixestates.exception;

public class InvalidRequestException extends IdpException {

    private static final long serialVersionUID = 1L;

    private String param;

    public InvalidRequestException(final String message) {
        super(message, null);
        this.param = null;
    }

    public InvalidRequestException(final String message, final Throwable cause) {
        super(message, cause);
        this.param = null;
    }

    public InvalidRequestException(final String message, final String param) {
        super(message, null);
        this.param = param;
    }

    public InvalidRequestException(final String message, final String param, final Throwable cause) {
        super(message, cause);
        this.param = param;
    }

    public String getParam() {
        return param;
    }

}