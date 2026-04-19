package com.skroflin.evoting_rest_api.exceptions.election;

public class ElectionNotStartedException extends RuntimeException {
    public ElectionNotStartedException(String message) {
        super(message);
    }
}
