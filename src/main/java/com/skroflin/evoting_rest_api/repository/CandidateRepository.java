package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, UUID> {

    List<Candidate> findAllByElectionElectionUUID(UUID electionId);
    Optional<Candidate> findByCandidateUUIDAndElectionElectionUUID(UUID candidateId, UUID electionId);
    boolean existsByCandidateFullNameAndElectionElectionUUID(String fullName, UUID electionId);
}
