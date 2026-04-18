package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.ValidatedVoteData;
import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.dto.response.CandidateResultResponse;
import com.skroflin.evoting_rest_api.dto.response.ElectionResultResponse;
import com.skroflin.evoting_rest_api.dto.response.VoteResponse;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.exceptions.ResourceNotFoundException;
import com.skroflin.evoting_rest_api.exceptions.TokenAlreadyUsedException;
import com.skroflin.evoting_rest_api.exceptions.election.ElectionNotOpenException;
import com.skroflin.evoting_rest_api.models.Candidate;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.models.UsedToken;
import com.skroflin.evoting_rest_api.models.Vote;
import com.skroflin.evoting_rest_api.repository.CandidateRepository;
import com.skroflin.evoting_rest_api.repository.ElectionRepository;
import com.skroflin.evoting_rest_api.repository.UsedTokenRepository;
import com.skroflin.evoting_rest_api.repository.VoteRepository;
import com.skroflin.evoting_rest_api.service.CryptoService;
import com.skroflin.evoting_rest_api.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static com.skroflin.evoting_rest_api.util.HashingUtils.*;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final CryptoService cryptoService;
    private final VoteRepository voteRepository;
    private final ElectionRepository electionRepository;
    private final CandidateRepository candidateRepository;
    private final UsedTokenRepository usedTokenRepository;

    @Override
    public VoteResponse castVote(UUID electionId, VoteRequest voteRequest) {
        ValidatedVoteData voteData = validateAndGetVoteData(electionId, voteRequest);

        UsedToken usedToken = new UsedToken();
        usedToken.setTokenHash(voteData.hashedToken());
        usedToken.setUsedAt(LocalDateTime.now());
        usedToken.setCreatedAt(LocalDateTime.now());

        UsedToken savedToken = usedTokenRepository.save(usedToken);

        Vote vote = new Vote();
        vote.setElection(voteData.election());
        vote.setCandidate(voteData.candidate());
        vote.setUsedToken(savedToken);
        vote.setCastAt(LocalDateTime.now());
        vote.setCreatedAt(LocalDateTime.now());

        String signature = cryptoService.generateSignature(vote);
        vote.setSignature(signature);

        Vote savedVote = voteRepository.save(vote);

        return new VoteResponse(
                savedVote.getVoteUUID(),
                savedVote.getCastAt(),
                "Your vote has been successfully cast and recorded anonymously.",
                voteData.election().getElectionName()
        );
    }

    @Override
    public ElectionResultResponse getResults(UUID electionId) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found: " + electionId));

        LocalDateTime now = LocalDateTime.now();
        boolean isClosed = election.getElectionEndTime() != null && now.isAfter(election.getElectionEndTime());

        if (!isClosed) {
            return new ElectionResultResponse(
                    election.getElectionName(),
                    false,
                    List.of(),
                    "The election results will be available after the elections: " + election.getElectionEndTime()
            );
        }

        List<Object[]> rawResults = voteRepository.countVotesByCandidates(electionId);

        List<CandidateResultResponse> results = rawResults.stream()
                .map(row -> new CandidateResultResponse(
                        (String) row[0],
                        (UUID) row[1],
                        (Long) row[2]))
                .toList();

        return new ElectionResultResponse(
                election.getElectionName(),
                true,
                results,
                "Final election results"
        );
    }

    private ValidatedVoteData validateAndGetVoteData(UUID electionId, VoteRequest voteRequest) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        if (election.getElectionStatus() != ElectionStatus.ACTIVE) {
            throw new ElectionNotOpenException("Election is: " + election.getElectionStatus() + ", voting is not allowed");
        }

        String hashedToken = hashToken(voteRequest.token());
        if (usedTokenRepository.existsById(hashedToken)) {
            throw new TokenAlreadyUsedException("This token is already used");
        }

        Candidate candidate = candidateRepository.findByCandidateUUIDAndElectionElectionUUID(
                voteRequest.candidateUUID(), electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Candidate not found"));

        return new ValidatedVoteData(election, candidate, hashedToken);
    }
}
