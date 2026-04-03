package com.skroflin.evoting_rest_api.exceptions.user.verification;

public class VerificationCodeExpiredException extends RuntimeException {
    public VerificationCodeExpiredException(String message) {
        super(message);
    }
}
