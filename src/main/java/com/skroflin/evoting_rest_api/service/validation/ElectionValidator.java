package com.skroflin.evoting_rest_api.service.validation;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.exceptions.election.ElectionAlreadyExistsException;
import com.skroflin.evoting_rest_api.exceptions.election.InvalidElectionException;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.repository.ElectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ElectionValidator {

    private final ElectionRepository electionRepository;

    public void validateElectionRequest(ElectionRequest electionRequest) {
        if (!electionRequest.startDate().isBefore(electionRequest.endDate())) {
            throw new InvalidElectionException("Election end date must be after the start date");
        }

        if (Duration.between(electionRequest.startDate(), electionRequest.endDate()).toMinutes() < 240) {
            throw new InvalidElectionException("Election must be at least 4 hours");
        }

        if (electionRepository.existsByElectionName(electionRequest.title())) {
            throw new ElectionAlreadyExistsException("An election with this title already exists");
        }
    }

    public void validateStatusTransition(ElectionStatus currentStatus, ElectionStatus newStatus) {
        if (currentStatus == ElectionStatus.CLOSED && newStatus != ElectionStatus.CLOSED) {
            throw new IllegalArgumentException("Cannot change status of an election that has already finished");
        }
    }
}
