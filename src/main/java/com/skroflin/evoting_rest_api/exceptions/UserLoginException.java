package com.skroflin.evoting_rest_api.exceptions;

public class UserLoginException extends RuntimeException {
    public UserLoginException(String message) {
        super(message);
    }
}
