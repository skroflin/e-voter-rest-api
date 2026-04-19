package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
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
import com.skroflin.evoting_rest_api.util.HashingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VoteServiceImplTest {

    @Mock
    private VoteRepository voteRepository;
    @Mock
    private ElectionRepository electionRepository;
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private UsedTokenRepository usedTokenRepository;
    @Mock
    private CryptoService cryptoService;

    @InjectMocks
    private VoteServiceImpl voteService;

    private UUID mockElectionId;
    private UUID mockCandidateId;
    private VoteRequest mockVoteRequest;
    private Election mockElection;
    private Candidate mockCandidate;

    @BeforeEach
    void setUp() {
        mockElectionId = UUID.randomUUID();
        mockCandidateId = UUID.randomUUID();
        mockVoteRequest = new VoteRequest(mockCandidateId, "secret-mock-token");

        mockElection = new Election();
        mockElection.setElectionUUID(mockElectionId);
        mockElection.setElectionName("Test Election");
        mockElection.setElectionStatus(ElectionStatus.ACTIVE);

        mockCandidate = new Candidate();
        mockCandidate.setCandidateUUID(mockCandidateId);
        mockCandidate.setElection(mockElection);
    }

    @Test
    void castVote_success() {
        String hashedToken = HashingUtils.hashToken(mockVoteRequest.token());

        when(electionRepository.findById(mockElectionId)).thenReturn(Optional.of(mockElection));
        when(usedTokenRepository.existsById(hashedToken)).thenReturn(false);
        when(candidateRepository.findByCandidateUUIDAndElectionElectionUUID(mockCandidateId, mockElectionId)).thenReturn(Optional.of(mockCandidate));

        Vote mockSavedVote = new Vote();
        mockSavedVote.setVoteUUID(UUID.randomUUID());
        mockSavedVote.setCastAt(LocalDateTime.now());
        mockSavedVote.setElection(mockElection);

        when(cryptoService.generateSignature(any(Vote.class))).thenReturn("mock-signature-12345");

        when(usedTokenRepository.save(any(UsedToken.class))).thenAnswer(i -> i.getArguments()[0]);
        when(voteRepository.save(any(Vote.class))).thenReturn(mockSavedVote);

        VoteResponse mockVoteResponse = voteService.castVote(mockElectionId, mockVoteRequest);

        assertNotNull(mockVoteResponse);
        assertEquals("Test Election", mockVoteResponse.electionName());
        verify(voteRepository, times(1)).save(any(Vote.class));
        verify(usedTokenRepository, times(1)).save(any(UsedToken.class));
    }

    @Test
    void castVote_ThrowsElectionNotOpenException() {
        mockElection.setElectionStatus(ElectionStatus.CLOSED);
        when(electionRepository.findById(mockElectionId)).thenReturn(Optional.of(mockElection));

        assertThrows(ElectionNotOpenException.class, () -> voteService.castVote(mockElectionId, mockVoteRequest));
        verify(voteRepository, never()).save(any());
    }

    @Test
    void castVote_ThrowsTokenAlreadyUsedExcepiton() {
        String mockHashedToken = HashingUtils.hashToken(mockVoteRequest.token());
        when(electionRepository.findById(mockElectionId)).thenReturn(Optional.of(mockElection));
        when(usedTokenRepository.existsById(mockHashedToken)).thenReturn(true);

        assertThrows(TokenAlreadyUsedException.class, () -> voteService.castVote(mockElectionId, mockVoteRequest));
    }

    @Test
    void getResults_ShouldReturnResults_WhenElectionClosed() {
        UUID mockElectionId = UUID.randomUUID();

        Election mockElection = new Election();
        mockElection.setElectionName("Test Election");
        mockElection.setDescription("Test description");
        mockElection.setElectionEndTime(LocalDateTime.now().minusHours(1));
        mockElection.setElectionStatus(ElectionStatus.CLOSED);

        Object[] row = new Object[]{"John Doe", UUID.randomUUID(), 10L};
        List<Object[]> mockRawResults = (List<Object[]>) List.of(row);

        when(electionRepository.findById(mockElectionId)).thenReturn(Optional.of(mockElection));
        when(voteRepository.countVotesByCandidates(mockElectionId)).thenReturn((List) mockRawResults);

        ElectionResultResponse mockElectionResult = voteService.getResults(mockElectionId);

        assertTrue(mockElectionResult.isClosed());
        assertEquals(1, mockElectionResult.results().size());
        assertEquals("John Doe", mockElectionResult.results().get(0).fullName());
        assertEquals(10L, mockElectionResult.results().get(0).voteCount());
        verify(voteRepository, times(1)).countVotesByCandidates(mockElectionId);
    }

    @Test
    void getResults_shouldHideResults_whenElectionActive() {
        UUID mockElectionId = UUID.randomUUID();

        Election mockElection = new Election();
        mockElection.setElectionName("Test Election");
        mockElection.setDescription("Test description");
        mockElection.setElectionEndTime(LocalDateTime.now().plusDays(1));
        mockElection.setElectionStatus(ElectionStatus.ACTIVE);

        when(electionRepository.findById(mockElectionId)).thenReturn(Optional.of(mockElection));

        ElectionResultResponse mockElectionResult = voteService.getResults(mockElectionId);

        assertFalse(mockElectionResult.isClosed());
        assertTrue(mockElectionResult.results().isEmpty());
        assertTrue(mockElectionResult.message().contains("The election results will be available after the elections: " + mockElection.getElectionEndTime()));

        verify(voteRepository, never()).countVotesByCandidates(any());
    }

    @Test
    void getResults_shouldThrowException_whenNotFound() {
        UUID electionId = UUID.randomUUID();

        when(electionRepository.findById(electionId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> voteService.getResults(electionId));
    }
}
