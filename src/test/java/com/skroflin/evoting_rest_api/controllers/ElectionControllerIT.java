package com.skroflin.evoting_rest_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skroflin.evoting_rest_api.dto.request.CandidateRequest;
import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import com.skroflin.evoting_rest_api.models.Election;
import com.skroflin.evoting_rest_api.repository.ElectionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ElectionControllerIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ElectionRepository electionRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /elections - Admin should create election, return 201")
    void createElection_AdminSuccess() throws Exception {
        ElectionRequest mockElectionRequest = new ElectionRequest(
                "Test izbori 2026", "Opis",
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                List.of(new CandidateRequest("Testni kandidat", "Testni opis"))
        );

        mockMvc.perform(post("/api/v1/elections")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(mockElectionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test izbori 2026"));

        assertTrue(electionRepository.existsByElectionName("Test izbori 2026"));
    }

    @Test
    @WithMockUser(roles = "VOTER")
    @DisplayName("POST /elections - Voter shouldn't create election, return 403")
    void createElection_VoterFailure() throws Exception {
        ElectionRequest mockElectionRequest = new ElectionRequest(
                "Test izbori 2026", "Opis",
                null, null, List.of()
        );

        mockMvc.perform(post("/api/v1/elections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockElectionRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "VOTER")
    @DisplayName("GET /elections - Should return paged elections")
    void getAllElections_PagedSuccess() throws Exception {

        Election mockElection = new Election();
        mockElection.setElectionName("Test Election");
        mockElection.setDescription("Test description");
        mockElection.setElectionStartTime(LocalDateTime.now().plusDays(1));
        mockElection.setElectionEndTime(LocalDateTime.now().plusDays(2));
        mockElection.setElectionStatus(ElectionStatus.UNKNOWN);

        electionRepository.save(mockElection);

        mockMvc.perform(get("/api/v1/elections")
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.size").value(5))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /elections/{id} - Return election by given id")
    void getElectionById_Success() throws Exception {
        Election mockElection = new Election();
        mockElection.setElectionName("Test Election");
        mockElection.setDescription("Test description");
        mockElection.setElectionStartTime(LocalDateTime.now().plusDays(1));
        mockElection.setElectionEndTime(LocalDateTime.now().plusDays(2));
        mockElection.setElectionStatus(ElectionStatus.UNKNOWN);

        Election savedElection = electionRepository.save(mockElection);

        UUID mockId = savedElection.getElectionUUID();
        mockMvc.perform(get("/api/v1/elections/{id}", mockId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockId.toString()));
    }

    @Test
    @WithMockUser(roles = "VOTER")
    @DisplayName("GET /elections/{id} - Return not found status")
    void getElectionById_NotFound() throws Exception {
        UUID randomMockId = UUID.randomUUID();
        mockMvc.perform(get("/api/v1/elections/{id}", randomMockId))
                .andExpect(status().isNotFound());
    }
}
