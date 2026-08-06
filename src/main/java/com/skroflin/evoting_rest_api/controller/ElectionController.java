package com.skroflin.evoting_rest_api.controller;

import com.skroflin.evoting_rest_api.dto.request.CandidateRequest;
import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.request.ElectionStatusUpdateRequest;
import com.skroflin.evoting_rest_api.dto.response.CandidateResponse;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.filter.ElectionFilter;
import com.skroflin.evoting_rest_api.service.ElectionService;
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
@RequestMapping("/api/v1/elections")
@RequiredArgsConstructor
public class ElectionController {

    private final ElectionService electionService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ELECTION_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ElectionResponse> createElection(@RequestBody @Valid ElectionRequest electionRequest) {
        ElectionResponse electionResponse = electionService.createElection(electionRequest);
        return new ResponseEntity<>(electionResponse, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyAuthority('ROLE_ELECTION_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<ElectionResponse> updateElectionStatus(
            @PathVariable UUID id,
            @RequestBody @Valid ElectionStatusUpdateRequest request
    ) {
        ElectionResponse response = electionService.updateElectionStatus(id, request.electionStatus());
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ELECTION_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_VOTER')")
    public ResponseEntity<Page<ElectionResponse>> getAllElections(
            @PageableDefault(page = 0, size = 10, sort = "electionUUID", direction = Sort.Direction.ASC)
            Pageable pageable,
            ElectionFilter electionFilter
    ) {
        return ResponseEntity.ok(electionService.getAllElections(pageable, electionFilter));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ELECTION_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_VOTER')")
    public ResponseEntity<ElectionResponse> getElectionById(@PathVariable UUID id) {
        return ResponseEntity.ok(electionService.getElectionById(id));
    }

    @PostMapping("/{electionId}/add-candidate")
    @PreAuthorize("hasAnyAuthority('ROLE_ELECTION_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<CandidateResponse> addCandidateToElection(
            @PathVariable UUID electionId,
            @Valid @RequestBody CandidateRequest candidateRequest
    ) {
        CandidateResponse candidateResponse = electionService.addCandidateToElection(electionId, candidateRequest);
        return new ResponseEntity<>(candidateResponse, HttpStatus.CREATED);
    }
}