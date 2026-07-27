package com.skroflin.evoting_rest_api.controller;

import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResultResponse;
import com.skroflin.evoting_rest_api.dto.response.VoteResponse;
import com.skroflin.evoting_rest_api.dto.response.VoterVoteHistoryResponse;
import com.skroflin.evoting_rest_api.dto.response.VotingTokenResponse;
import com.skroflin.evoting_rest_api.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{electionId}/generate-token")
    @PreAuthorize("hasAuthority('ROLE_VOTER')")
    public ResponseEntity<VotingTokenResponse> generateVotingToken(@PathVariable UUID electionId) {
        String token = voteService.generateVotingToken(electionId);
        return ResponseEntity.ok(new VotingTokenResponse(token));
    }

    @PostMapping("/{electionId}/vote")
    @PreAuthorize("hasRole('ROLE_VOTER')")
    public ResponseEntity<VoteResponse> castVote(
            @PathVariable UUID electionId,
            @Valid @RequestBody VoteRequest voteRequest
    ) {
        VoteResponse voteResponse = voteService.castVote(electionId, voteRequest);
        return new ResponseEntity<>(voteResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{electionId}/results")
    @PreAuthorize("hasAnyAuthority('ROLE_ELECTION_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_VOTER')")
    public ResponseEntity<ElectionResultResponse> getResults(@PathVariable UUID electionId) {
        ElectionResultResponse response = voteService.getResults(electionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-votes")
    @PreAuthorize("hasRole('ROLE_VOTER')")
    public ResponseEntity<Page<VoterVoteHistoryResponse>> getMyVoteHistory(
            @PageableDefault(page = 0, size = 10, sort = "votedAt", direction = Sort.Direction.ASC)
            Pageable pageable
    ) {
        return ResponseEntity.ok(voteService.getVoterHistory(pageable));
    }
}
