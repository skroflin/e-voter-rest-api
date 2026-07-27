package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.config.security.SecurityUtil;
import com.skroflin.evoting_rest_api.dto.ValidatedVoteData;
import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.dto.response.CandidateResultResponse;
import com.skroflin.evoting_rest_api.dto.response.ElectionResultResponse;
import com.skroflin.evoting_rest_api.dto.response.VoteResponse;
import com.skroflin.evoting_rest_api.dto.response.VoterVoteHistoryResponse;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.exceptions.AlreadyVotedException;
import com.skroflin.evoting_rest_api.exceptions.ResourceNotFoundException;
import com.skroflin.evoting_rest_api.exceptions.TokenAlreadyUsedException;
import com.skroflin.evoting_rest_api.exceptions.election.ElectionEndedException;
import com.skroflin.evoting_rest_api.exceptions.election.ElectionNotOpenException;
import com.skroflin.evoting_rest_api.exceptions.election.ElectionNotStartedException;
import com.skroflin.evoting_rest_api.models.*;
import com.skroflin.evoting_rest_api.repository.*;
import com.skroflin.evoting_rest_api.service.CryptoService;
import com.skroflin.evoting_rest_api.service.VoteService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final IssuedTokenRepository issuedTokenRepository;
    private final ElectionParticipationRepository electionParticipationRepository;
    private final EligibleVoterRepository eligibleVoterRepository;

    @Override
    @Transactional
    public String generateVotingToken(UUID electionId) {
        String currentUsername = SecurityUtil.getCurrentUserEmail();

        EligibleVoter voter = eligibleVoterRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found with email: " + currentUsername));

        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found with id: " + electionId));

        if (electionParticipationRepository.existsByEligibleVoterEmailAndElectionElectionUUID(voter.getEmail(), election.getElectionUUID())) {
            throw new AlreadyVotedException("Voting token has already been issued or user already voted in this election!");
        }

        String rawVotingToken = UUID.randomUUID() + "-" + UUID.randomUUID();

        IssuedToken issuedToken = new IssuedToken();
        issuedToken.setTokenUUID(UUID.randomUUID());
        issuedToken.setVoter(voter);
        issuedToken.setIssuedAt(LocalDateTime.now());
        issuedToken.setCreatedAt(LocalDateTime.now());
        issuedTokenRepository.save(issuedToken);

        ElectionParticipation participation = new ElectionParticipation();
        participation.setEligibleVoter(voter);
        participation.setElection(election);
        participation.setVotedAt(LocalDateTime.now());
        electionParticipationRepository.save(participation);

        return rawVotingToken;
    }

    @Override
    public Page<VoterVoteHistoryResponse> getVoterHistory(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserEmail();
        return electionParticipationRepository.findHistoryByVoterEmail(email, pageable);
    }

    @Override
    @Transactional
    public VoteResponse castVote(UUID electionId, VoteRequest voteRequest) {

        ValidatedVoteData voteData = validateAndGetVoteData(electionId, voteRequest);

        String currentUserEmail = SecurityUtil.getCurrentUserEmail();
        if (electionParticipationRepository.existsByEligibleVoterEmailAndElectionElectionUUID(currentUserEmail, electionId)) {
            throw new AlreadyVotedException("User already voted on this election");
        }

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

        if (election.getElectionEndTime() == null ||LocalDateTime.now().isBefore(election.getElectionEndTime())) {
            return new ElectionResultResponse(
                    election.getElectionName(),
                    false,
                    List.of(),
                    "Results available after: " + election.getElectionEndTime()
            );
        }

        List<CandidateResultResponse> resultResponses = voteRepository.countVotesByCandidates(electionId);

        return new ElectionResultResponse(
                election.getElectionName(),
                true,
                resultResponses,
                "Final election results"
        );
    }

    private ValidatedVoteData validateAndGetVoteData(UUID electionId, VoteRequest voteRequest) {
        Election election = electionRepository.findById(electionId)
                .orElseThrow(() -> new ResourceNotFoundException("Election not found"));

        if (election.getElectionStartTime() != null && LocalDateTime.now().isBefore(election.getElectionStartTime())) {
            throw new ElectionNotStartedException("Election has not started yet");
        }

        if (election.getElectionEndTime() != null && LocalDateTime.now().isAfter(election.getElectionEndTime())) {
            throw new ElectionEndedException("Election has already ended");
        }

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
