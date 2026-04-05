package com.skroflin.evoting_rest_api.exceptions.election;

public class ElectionAlreadyExistsException extends RuntimeException {
    public ElectionAlreadyExistsException(String message) {
        super(message);
    }
}
