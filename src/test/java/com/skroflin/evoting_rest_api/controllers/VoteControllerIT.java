package com.skroflin.evoting_rest_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skroflin.evoting_rest_api.dto.request.VoteRequest;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.models.Candidate;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.models.ElectionParticipation;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.repository.*;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class VoteControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionParticipationRepository electionParticipationRepository;

    @Autowired
    private EligibleVoterRepository eligibleVoterRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private final String TEST_EMAIL = "test@example.com";

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

        mockMvc.perform(post("/api/v1/votes/{electionId}/vote", mockElection.getElectionUUID())
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

        mockMvc.perform(get("/api/v1/votes/{electionId}/results", savedMockElection.getElectionUUID()))
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

        mockMvc.perform(get("/api/v1/votes/{electionId}/results", savedMockElection.getElectionUUID()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isClosed").value(false))
                .andExpect(jsonPath("$.electionName").value("Test Election 2"))
                .andExpect(jsonPath("$.message").value(Matchers.containsString("The election results will be available after the elections: " + mockElection.getElectionEndTime())));
    }

    @BeforeEach
    void setUp() {
        voteRepository.deleteAll();
        electionParticipationRepository.deleteAll();
        candidateRepository.deleteAll();
        electionRepository.deleteAll();
        eligibleVoterRepository.deleteAll();

        EligibleVoter mockVoter = new EligibleVoter();
        mockVoter.setEmail(TEST_EMAIL);
        mockVoter.setUsername("jdoe");
        mockVoter.setPasswordHash("O7xu6U409JvCcEFD");
        mockVoter.setFirstName("John");
        mockVoter.setLastName("Doe");

        eligibleVoterRepository.save(mockVoter);

        Election mockElection = new Election();
        mockElection.setElectionName("Test Election");
        electionRepository.save(mockElection);

        ElectionParticipation mockElectionParticipation = new ElectionParticipation();
        mockElectionParticipation.setEligibleVoter(mockVoter);
        mockElectionParticipation.setElection(mockElection);
        mockElectionParticipation.setVotedAt(LocalDateTime.now());
        electionParticipationRepository.save(mockElectionParticipation);
    }

    @Test
    @WithMockUser(username = TEST_EMAIL, roles = "VOTER")
    void shouldReturnVoterHistory_success() throws Exception {
        mockMvc.perform(get("/api/v1/votes/my-votes")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].electionName", is("Test Election")))
                .andExpect(jsonPath("$.totalElements", is(1)));
    }

    @Test
    @WithMockUser(username = "unknownUser@example.com", roles = "VOTER")
    void shouldReturnEmptyHistory_whenUserHasNoParticipations() throws Exception {
        mockMvc.perform(get("/api/v1/votes/my-votes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void shouldReturnForbidden_whenAdminTriesToViewVotingHistory() throws Exception {
        mockMvc.perform(get("/api/v1/votes/my-votes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
