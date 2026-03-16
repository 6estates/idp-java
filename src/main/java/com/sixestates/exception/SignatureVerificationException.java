package com.sixestates.exception;

/**
 * @author kechen, 27/09/24.
 */
public class SignatureVerificationException extends IdpException {

    private String sigHeader;

    public SignatureVerificationException(String message, String sigHeader) {
        super(message);
        this.sigHeader = sigHeader;
    }

    public SignatureVerificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
