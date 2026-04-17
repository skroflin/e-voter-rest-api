package com.skroflin.evoting_rest_api.exceptions;

public class VerifySignatureException extends RuntimeException {
    public VerifySignatureException(String message) {
        super(message);
    }
}
