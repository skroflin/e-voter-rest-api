package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.response.UserResponse;
import org.springframework.security.core.Authentication;

public interface UserServce {

    UserResponse getCurrentUserProfile(Authentication authentication);
}
