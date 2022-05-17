package com.sixestates.exception;

public abstract class IdpException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IdpException(final String message) {
        this(message, null);
    }

    public IdpException(final String message, final Throwable cause) {
        super(message, cause);
    }
}