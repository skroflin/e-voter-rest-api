package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.RegisterRequest;
import com.skroflin.evoting_rest_api.mappers.AuthMapper;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import com.skroflin.evoting_rest_api.repository.UserVerificationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class EligibleVoterServiceTest {

    @Mock
    private EligibleVoterRepository eligibleVoterRepository;
    @Mock
    private AuthMapper authMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserVerificationRepository userVerificationRepository;

    @InjectMocks
    private EligibleVoterService eligibleVoterService;

    @Test
    @DisplayName("Registration should throw exception if the domain is invalid - @ffos.hr")
    void registerInvalidDomainException() {
        RegisterRequest registerRequest = new RegisterRequest(
          "Sven", "Kroflin", "sven.kroflin@gmail.com", "skroflin", "yBA244n7dqgi8BuS"
        );

        IllegalArgumentException domainException = assertThrows(IllegalArgumentException.class,
                () -> eligibleVoterService.registerVoter(registerRequest));

        assertEquals("@ffos.hr domain is only allowed", domainException.getMessage());
        verify(eligibleVoterRepository, never()).save(any());
    }

    @Test
    @DisplayName("Registration should be successful with the valid data")
    void registerValidData() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Sven", "Kroflin", "skroflin@ffos.hr", "skroflin", "OJvbGPqVJmmcCVzr"
        );

        EligibleVoter mockVoter = new EligibleVoter();
        when(eligibleVoterRepository.existbyEmail(anyString())).thenReturn(false);
        when(eligibleVoterRepository.existsByUsername(anyString())).thenReturn(false);
        when(authMapper.toEntity(any())).thenReturn(mockVoter);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(eligibleVoterRepository.save(any())).thenReturn(mockVoter);

        String result = eligibleVoterService.registerVoter(registerRequest);

        assertTrue(result.contains("Verification code sent"));
        verify(eligibleVoterRepository, times(1)).save(any());
        verify(userVerificationRepository, times(1)).save(any());
    }
}
