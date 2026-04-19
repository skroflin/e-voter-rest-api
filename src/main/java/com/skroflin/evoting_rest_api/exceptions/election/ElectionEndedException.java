package com.skroflin.evoting_rest_api.exceptions.election;

public class ElectionEndedException extends RuntimeException {
    public ElectionEndedException(String message) {
        super(message);
    }
}
