package com.skroflin.evoting_rest_api.exceptions.user.verification;

public class InvalidVerificationCodeException extends RuntimeException {
    public InvalidVerificationCodeException(String message) {
        super(message);
    }
}
