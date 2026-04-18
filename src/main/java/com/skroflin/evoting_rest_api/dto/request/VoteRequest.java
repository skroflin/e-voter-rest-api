package com.skroflin.evoting_rest_api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record VoteRequest(
        @NotNull(message = "Candidate id is required")
        UUID candidateUUID,
        @NotNull(message = "Vote token is required")
        String token
) { }
