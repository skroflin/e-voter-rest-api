package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.exceptions.ResourceNotFoundException;
import com.skroflin.evoting_rest_api.mappers.ElectionMapper;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.repository.ElectionRepository;
import com.skroflin.evoting_rest_api.service.ElectionService;
import com.skroflin.evoting_rest_api.service.validation.ElectionValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionMapper electionMapper;
    private final ElectionValidator electionValidator;

    @Override
    @Transactional
    public ElectionResponse createElection(ElectionRequest electionRequest) {

        electionValidator.validateElectionRequest(electionRequest);
        Election election = electionMapper.toEntity(electionRequest);

        election.setElectionStatus(ElectionStatus.PREPARATION);
        election.setCreatedAt(LocalDateTime.now());

        election.setPublicKey("rsa-pub-" + UUID.randomUUID());
        election.setPrivateKeyEnc("rsa-priv-enc-" + UUID.randomUUID());

        Election savedElection = electionRepository.save(election);
        return electionMapper.toResponse(savedElection);
    }

    @Override
    @Transactional
    public Page<ElectionResponse> getAllElections(Pageable pageable) {
        return electionRepository.findAll(pageable)
                .map(electionMapper::toResponse);
    }

    @Override
    @Transactional
    public ElectionResponse getElectionById(UUID id) {
        return electionRepository.findById(id)
                .map(electionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Election with id: " + id + " not found"));
    }
}
