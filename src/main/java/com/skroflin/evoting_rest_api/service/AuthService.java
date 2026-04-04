package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.LoginRequest;
import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;
import com.skroflin.evoting_rest_api.dto.request.VerificationRequest;
import com.skroflin.evoting_rest_api.dto.response.LoginResponse;

public interface AuthService {

    String registerVoter(RegisterRequest registerRequest);
    void verifyVoter(VerificationRequest verificationRequest);
    public LoginResponse login(LoginRequest request);
}
