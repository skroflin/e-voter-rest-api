package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.config.jwt.JwtService;
import com.skroflin.evoting_rest_api.dto.request.LoginRequest;
import com.skroflin.evoting_rest_api.dto.response.LoginResponse;
import com.skroflin.evoting_rest_api.enums.Role;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoginEligibleVoterServiceTest {

    @Mock
    private EligibleVoterRepository eligibleVoterRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private EligibleVoterService eligibleVoterService;

    private EligibleVoter testVoter;
    private LoginRequest testLoginRequest;

    @BeforeEach
    void setUp() {
        testVoter = new EligibleVoter();
        testVoter.setUsername("test");
        testVoter.setEmail("test@ffos.hr");
        testVoter.setEnabled(true);
        testVoter.setRole(Role.ROLE_VOTER);

        testLoginRequest = new LoginRequest("test", "jx487WJ0n2WPqWWG");
    }

    @Test
    void Login_returnSuccessWhenValidCredentials() {
        when(eligibleVoterRepository.findByEmail(anyString())).thenReturn(Optional.of(testVoter));
        when(jwtService.generateToken(any())).thenReturn("mocked-jwt-token");
        when(jwtService.getExpirationTime()).thenReturn(7200000L);

        LoginResponse response = eligibleVoterService.login(testLoginRequest);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        assertEquals("test", response.username());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void Login_returnFailureWhenInvalidCredentials() {
        // TODO: add test for invalid credentials
    }

    @Test
    void Login_returnDisabledWhenUserNotEnabled() {
        // TODO: add test for non enabled user
    }

    @Test
    void Login_whenLoggingInWithEmail() {
        // TODO: add test for logging in with email
    }
}
