package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    List<Candidate> findAllByElectionElectionUUID(UUID electionId);
    Optional<Candidate> findByCandidateUUIDAndElectionElectionUUID(UUID candidateId, UUID electionId);
    boolean existsByCandidateFullNameAndElectionElectionUUID(String fullName, UUID electionId);
}
