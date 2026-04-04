package com.skroflin.evoting_rest_api.dto.response;

import java.util.UUID;

public record CandidateResponse(
        UUID id,
        String name,
        String bio
) {
}
