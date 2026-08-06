package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.CandidateRequest;
import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.CandidateResponse;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.filter.ElectionFilter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ElectionService {

    ElectionResponse createElection(ElectionRequest electionRequest);
    Page<ElectionResponse> getAllElections(Pageable pageable, ElectionFilter electionFilter);
    ElectionResponse getElectionById(UUID id);
    CandidateResponse addCandidateToElection(UUID electionId, CandidateRequest candidateRequest);
    ElectionResponse updateElectionStatus(UUID electionId, ElectionStatus newElectionStatus);
}
