package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResultResponse;
import com.skroflin.evoting_rest_api.dto.response.VoteResponse;

import java.util.UUID;

public interface VoteService {

    VoteResponse castVote(UUID electionId, VoteRequest voteRequest);
    ElectionResultResponse getResults(UUID electionId);
}
