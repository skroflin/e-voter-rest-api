package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;
import com.skroflin.evoting_rest_api.dto.request.VerificationRequest;
import com.skroflin.evoting_rest_api.exceptions.ResourceNotFoundException;
import com.skroflin.evoting_rest_api.mappers.AuthMapper;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.models.UserVerification;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import com.skroflin.evoting_rest_api.repository.UserVerificationRepository;
import com.skroflin.evoting_rest_api.service.EmailService;
import com.skroflin.evoting_rest_api.service.validation.VerificationHelper;
import com.skroflin.evoting_rest_api.service.validation.VoterValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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
    @Mock
    private EmailService emailService;
    @Mock
    private VoterValidator voterValidator;
    @Mock
    private VerificationHelper verificationHelper;
    @InjectMocks
    private EligibleVoterService eligibleVoterService;

    @Test
    @DisplayName("Registration should throw exception if the domain is invalid - @ffos.hr")
    void registerInvalidDomainException_Failure() {
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
    void registerValidData_Success() {
        RegisterRequest registerRequest = new RegisterRequest(
                "Sven", "Kroflin", "skroflin@ffos.hr", "skroflin", "OJvbGPqVJmmcCVzr"
        );

        EligibleVoter mockVoter = new EligibleVoter();
        when(eligibleVoterRepository.existsByEmail(anyString())).thenReturn(false);
        when(eligibleVoterRepository.existsByUsername(anyString())).thenReturn(false);
        when(authMapper.toEntity(any())).thenReturn(mockVoter);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(eligibleVoterRepository.save(any())).thenReturn(mockVoter);

        String result = eligibleVoterService.registerVoter(registerRequest);

        assertTrue(result.contains("Verification code sent"));
        verify(eligibleVoterRepository, times(1)).save(any());
        verify(userVerificationRepository, times(1)).save(any());
        verify(emailService, times(1)).sendVerificationEmail(eq("skroflin@ffos.hr"), anyString());
    }

    @Test
    @DisplayName("Verification should be successful with the valid verification code and email")
    void verifyVoter_Success() {
        String email = "skroflin@ffos.hr";
        String code = "678910";
        VerificationRequest mockRequest = new VerificationRequest(email, code);

        EligibleVoter mockVoter = new EligibleVoter();
        mockVoter.setEmail(email);
        mockVoter.setEnabled(false);

        UserVerification mockVerification = new UserVerification();
        mockVerification.setVerificationCode(code);

        when(eligibleVoterRepository.findByEmail(email)).thenReturn(Optional.of(mockVoter));
        when(userVerificationRepository.findByEligibleVoter(mockVoter)).thenReturn(Optional.of(mockVerification));

        eligibleVoterService.verifyVoter(mockRequest);

        assertTrue(mockVoter.isEnabled(), "Voter should be enabled after verification");
        verify(eligibleVoterRepository).save(mockVoter);
        verify(userVerificationRepository).delete(mockVerification);

//        when(verificationHelper.generateVerificationCode()).thenReturn("678910");
        verify(voterValidator).validateVoter(mockVerification, mockVoter, code);
    }

    @Test
    @DisplayName("Verification should be a failure with the invalid verification code and email")
    void verifyVoter_Failure() {
        String email = "test@ffos.hr";
        VerificationRequest mockRequest = new VerificationRequest(email, "123456");

        when(eligibleVoterRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            eligibleVoterService.verifyVoter(mockRequest);
        });
    }
}
