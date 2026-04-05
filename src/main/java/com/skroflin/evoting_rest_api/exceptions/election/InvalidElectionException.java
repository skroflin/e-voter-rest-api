package com.skroflin.evoting_rest_api.exceptions.election;

public class InvalidElectionException extends RuntimeException {
    public InvalidElectionException(String message) {
        super(message);
    }
}
