package com.skroflin.evoting_rest_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.models.Candidate;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.repository.CandidateRepository;
import com.skroflin.evoting_rest_api.repository.ElectionRepository;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VoteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    @WithMockUser(roles = "VOTER")
    @DisplayName("POST /elections - Voter should cast vote, return 201")
    void shouldCastVote_Success() throws Exception {

        Election mockElection = new Election();
        mockElection.setElectionName("Test Election");
        mockElection.setDescription("Test description");
        mockElection.setElectionStartTime(LocalDateTime.now().minusDays(1));
        mockElection.setElectionEndTime(LocalDateTime.now().plusDays(1));
        mockElection.setElectionStatus(ElectionStatus.ACTIVE);
        mockElection = electionRepository.save(mockElection);

        Candidate mockCandidate = new Candidate();
        mockCandidate.setCandidateFullName("John Doe");
        mockCandidate.setDescription("Test");
        mockCandidate.setElection(mockElection);
        mockCandidate = candidateRepository.save(mockCandidate);

        VoteRequest mockVoteRequest = new VoteRequest(mockCandidate.getCandidateUUID(), "mock-token-123");

        mockMvc.perform(post("/api/v1/elections/{electionId}/vote", mockElection.getElectionUUID())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockVoteRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voteUUID").exists())
                .andExpect(jsonPath("$.electionName").value("Test Election"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @Transactional
    @WithMockUser(roles = "VOTER")
    @DisplayName("GET /results - Should show results when voting is closed")
    void testShouldShowResults_successWhenClosedVoting() throws Exception {
        Election mockElection = new Election();
        mockElection.setElectionName("Test Election 2");
        mockElection.setDescription("Test description 2");
        mockElection.setElectionStartTime(LocalDateTime.now().minusDays(2));
        mockElection.setElectionEndTime(LocalDateTime.now().minusMinutes(1));
        mockElection.setElectionStatus(ElectionStatus.CLOSED);

        Candidate mockCandidate = new Candidate();
        mockCandidate.setCandidateFullName("John Doe");
        mockCandidate.setDescription("Test");

        mockCandidate.setElection(mockElection);
        mockElection.getCandidates().add(mockCandidate);

        Election savedMockElection = electionRepository.saveAndFlush(mockElection);

        mockMvc.perform(get("/api/v1/elections/{electionId}/results", savedMockElection.getElectionUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isClosed").value(true))
                .andExpect(jsonPath("$.electionName").value("Test Election 2"));
    }

    @Test
    @Transactional
    @WithMockUser(roles = "VOTER")
    @DisplayName("GET /results - Should hide results and return empty list when election is still active")
    void testShouldShowEmptyResults_returnHiddenMessageWhenActive() throws Exception {
        Election mockElection = new Election();
        mockElection.setElectionName("Test Election 2");
        mockElection.setDescription("Test description 2");
        mockElection.setElectionStartTime(LocalDateTime.now().minusDays(1));
        mockElection.setElectionEndTime(LocalDateTime.now().plusDays(1));
        mockElection.setElectionStatus(ElectionStatus.ACTIVE);

        Candidate mockCandidate = new Candidate();
        mockCandidate.setCandidateFullName("John Doe");
        mockCandidate.setDescription("Test");

        mockCandidate.setElection(mockElection);
        mockElection.getCandidates().add(mockCandidate);

        Election savedMockElection = electionRepository.saveAndFlush(mockElection);

        mockMvc.perform(get("/api/v1/elections/{electionId}/results", savedMockElection.getElectionUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isClosed").value(false))
                .andExpect(jsonPath("$.electionName").value("Test Election 2"))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("The election results will be available after the elections: " + mockElection.getElectionEndTime())));
    }
}
