package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VoteRepository extends JpaRepository<Vote, UUID> {

    boolean existsByElectionElectionUUIDAndUsedTokenTokenHash(UUID electionUUID, String tokenHash);

    @Query("select v.candidate.candidateFullName as fullName, v.candidate.candidateUUID, count(v) from Vote v where v.election.electionUUID = :electionId group by v.candidate.candidateFullName, v.candidate.candidateUUID order by count(v) desc")
    List<Object[]> countVotesByCandidates(@Param("electionId") UUID electionId);

    long countByElectionElectionUUID(UUID electionId);
}
