package com.skroflin.evoting_rest_api.exceptions.user;

public class UnknownUserException extends RuntimeException {
    public UnknownUserException(String message) {
        super(message);
    }
}
