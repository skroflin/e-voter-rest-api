package com.skroflin.evoting_rest_api.exceptions;

public class VotingException extends RuntimeException {
    public VotingException(String message) {
        super(message);
    }
}
