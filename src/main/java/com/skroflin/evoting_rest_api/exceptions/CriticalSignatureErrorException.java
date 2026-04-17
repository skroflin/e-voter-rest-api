package com.skroflin.evoting_rest_api.exceptions;

public class CriticalSignatureErrorException extends RuntimeException {
    public CriticalSignatureErrorException(String message) {
        super(message);
    }
}
