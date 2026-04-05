package com.skroflin.evoting_rest_api.controller;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
