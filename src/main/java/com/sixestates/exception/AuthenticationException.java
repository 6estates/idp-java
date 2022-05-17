package com.sixestates.exception;

public class AuthenticationException extends IdpException {

    private static final long serialVersionUID = 1L;

    public AuthenticationException(final String message) {
        super(message);
    }
}
