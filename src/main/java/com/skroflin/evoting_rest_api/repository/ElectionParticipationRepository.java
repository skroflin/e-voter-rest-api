package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.dto.response.VoterVoteHistoryResponse;
import com.skroflin.evoting_rest_api.models.ElectionParticipation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ElectionParticipationRepository extends JpaRepository<ElectionParticipation, UUID> {

    boolean existsByEligibleVoterEmailAndElectionElectionUUID(String email, UUID electionId);

    @Query("""
        SELECT new com.skroflin.evoting_rest_api.dto.response.VoterVoteHistoryResponse(
            p.election.electionUUID,
            p.election.electionName,
            p.votedAt
        )
        FROM ElectionParticipation p
        WHERE p.eligibleVoter.email = :identifier OR p.eligibleVoter.username = :identifier
    """)
    Page<VoterVoteHistoryResponse> findHistoryByVoterEmail(@Param("identifier") String identifier, Pageable pageable);
}
