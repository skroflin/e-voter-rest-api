package com.skroflin.evoting_rest_api.controller;

import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResultResponse;
import com.skroflin.evoting_rest_api.dto.response.VoteResponse;
import com.skroflin.evoting_rest_api.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/elections")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping("/{electionId}/vote")
    @PreAuthorize("hasRole('VOTER')")
    public ResponseEntity<VoteResponse> castVote(
            @PathVariable UUID electionId,
            @Valid @RequestBody VoteRequest voteRequest
    ) {
        VoteResponse voteResponse = voteService.castVote(electionId, voteRequest);
        return new ResponseEntity<>(voteResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{electionId}/results")
    @PreAuthorize("hasAnyRole('VOTER', 'ADMIN')")
    public ResponseEntity<ElectionResultResponse> getResults(@PathVariable UUID electionId) {
        ElectionResultResponse response = voteService.getResults(electionId);
        return ResponseEntity.ok(response);
    }
}
