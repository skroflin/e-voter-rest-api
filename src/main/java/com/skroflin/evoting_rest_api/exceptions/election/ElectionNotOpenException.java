package com.skroflin.evoting_rest_api.exceptions.election;

public class ElectionNotOpenException extends RuntimeException {
    public ElectionNotOpenException(String message) {
        super(message);
    }
}
