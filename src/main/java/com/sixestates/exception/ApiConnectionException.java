package com.sixestates.exception;

public class ApiConnectionException extends IdpException {

    private static final long serialVersionUID = 1L;

    public ApiConnectionException(final String message) {
        super(message);
    }

    public ApiConnectionException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
