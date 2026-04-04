package com.skroflin.evoting_rest_api.dto.response;

import lombok.Builder;

@Builder
public record LoginResponse(
    String token,
    String type,
    long expiresIn,
    String username,
    String role
) {
}
