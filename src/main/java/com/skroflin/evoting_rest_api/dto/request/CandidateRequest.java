package com.skroflin.evoting_rest_api.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CandidateRequest(
        @NotBlank(message = "Candidate name is mandatory")
        String name,
        String bio
) {
}
