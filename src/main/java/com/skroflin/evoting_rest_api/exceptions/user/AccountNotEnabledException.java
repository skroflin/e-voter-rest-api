package com.skroflin.evoting_rest_api.exceptions.user;

public class AccountNotEnabledException extends RuntimeException {
    public AccountNotEnabledException(String message) {
        super(message);
    }
}
