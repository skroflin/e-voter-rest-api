package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.RegisterRequest;

public interface AuthService {

    String registerVoter(RegisterRequest registerRequest);
}
