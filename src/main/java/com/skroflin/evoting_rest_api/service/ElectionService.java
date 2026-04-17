package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionService {

    ElectionResponse createElection(ElectionRequest electionRequest);
    Page<ElectionResponse> getAllElections(Pageable pageable);
    ElectionResponse getElectionById(UUID id);
}
