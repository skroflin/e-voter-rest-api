package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;

public interface AuthService {

    String registerVoter(RegisterRequest registerRequest);
}
