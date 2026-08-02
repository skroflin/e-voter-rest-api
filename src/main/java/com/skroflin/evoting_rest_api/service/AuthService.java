package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.*;
import com.skroflin.evoting_rest_api.dto.response.LoginResponse;

public interface AuthService {

    String registerVoter(RegisterRequest registerRequest);
    void verifyVoter(VerificationRequest verificationRequest);
    LoginResponse login(LoginRequest request);
    String processForgotPassword(ForgotPasswordRequest request);
    String processResetPassword(ResetPasswordRequest request);
}
