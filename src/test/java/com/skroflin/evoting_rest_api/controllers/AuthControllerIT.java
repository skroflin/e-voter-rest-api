package com.skroflin.evoting_rest_api.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.skroflin.evoting_rest_api.dto.RegisterRequest;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import com.skroflin.evoting_rest_api.service.EmailService;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AuthControllerIT {

    @Autowired
    private MockMvc mockMvc;

//    @MockitoBean
//    private EmailService emailService;

    @Autowired
    private EligibleVoterRepository eligibleVoterRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Test
    @DisplayName("POST /register - Should return 201")
    void registerVoterSuccess() throws Exception {

        RegisterRequest registerRequest = new RegisterRequest(
                "Sven", "Kroflin", "skroflin@ffos.hr", "skroflin", "ZTKuawVX9Ei7xGOD"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("Verification code sent")));

        assertTrue(eligibleVoterRepository.existsByUsername("skroflin"));
        Optional<EligibleVoter> savedVoter = eligibleVoterRepository.findByUsername("skroflin");
        assertFalse(savedVoter.get().isEnabled());
    }

    @Test
    @DisplayName("POST /register - Should return 400")
    void registerVoterFailure() throws Exception {

        RegisterRequest registerRequest = new RegisterRequest(
                "Sven", "Kroflin", "sven.kroflin@gmail.com", "skroflin", "ZTKuawVX9Ei7xGOD"
        );

        mockMvc.perform(post("/api/v1/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))

                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("@ffos.hr domain is only allowed"));
    }
}
