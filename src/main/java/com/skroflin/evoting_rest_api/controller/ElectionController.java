package com.skroflin.evoting_rest_api.controller;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ElectionResponse> createElection(@RequestBody @Valid ElectionRequest electionRequest) {
        ElectionResponse electionResponse = electionService.createElection(electionRequest);
        return new ResponseEntity<>(electionResponse, HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'VOTER')")
    public ResponseEntity<Page<ElectionResponse>> getAllElections(
            Pageable pageable) {
        return ResponseEntity.ok(electionService.getAllElections(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'VOTER')")
    public ResponseEntity<ElectionResponse> getElectionById(@PathVariable UUID id) {
        return ResponseEntity.ok(electionService.getElectionById(id));
    }
}
