package com.skroflin.evoting_rest_api.exceptions.election;

public class CandidateAlreadyExists extends RuntimeException {
    public CandidateAlreadyExists(String message) {
        super(message);
    }
}
