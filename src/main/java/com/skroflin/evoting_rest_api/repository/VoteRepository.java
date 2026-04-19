package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.dto.response.CandidateResultResponse;
import com.skroflin.evoting_rest_api.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends JpaRepository<Vote, UUID> {

    boolean existsByElectionElectionUUIDAndUsedTokenTokenHash(UUID electionUUID, String tokenHash);

    @Query("""
        select new com.skroflin.evoting_rest_api.dto.response.CandidateResultResponse(
                c.candidateFullName,
                c.candidateUUID,
                count(v)
            )
            from Candidate c
            left join Vote v on v.candidate = c
            where c.election.electionUUID = :electionId
            group by c.candidateFullName, c.candidateUUID
            order by count(v) desc
    """)
    List<CandidateResultResponse> countVotesByCandidates(@Param("electionId") UUID electionId);

    long countByElectionElectionUUID(UUID electionId);
}
