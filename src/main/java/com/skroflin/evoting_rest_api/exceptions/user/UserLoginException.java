package com.skroflin.evoting_rest_api.exceptions.user;

public class UserLoginException extends RuntimeException {
    public UserLoginException(String message) {
        super(message);
    }
}
