package com.skroflin.evoting_rest_api.exceptions.user;

public class InvalidEmailDomainException extends RuntimeException {
    public InvalidEmailDomainException(String message) {
        super(message);
    }
}
