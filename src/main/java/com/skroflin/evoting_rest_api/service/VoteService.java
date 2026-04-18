package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResultResponse;
import com.skroflin.evoting_rest_api.dto.response.VoteResponse;
import com.skroflin.evoting_rest_api.dto.response.VoterVoteHistoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface VoteService {

    VoteResponse castVote(UUID electionId, VoteRequest voteRequest);
    ElectionResultResponse getResults(UUID electionId);
    Page<VoterVoteHistoryResponse> getVoterHistory(Pageable pageable);
}
