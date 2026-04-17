package com.skroflin.evoting_rest_api.dto;

import com.skroflin.evoting_rest_api.models.Candidate;
import com.skroflin.evoting_rest_api.models.Election;

public record ValidatedVoteData(
    Election election,
    Candidate candidate,
    String hashedToken
) { }
