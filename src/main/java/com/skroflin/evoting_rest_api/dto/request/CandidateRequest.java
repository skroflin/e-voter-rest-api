package com.skroflin.evoting_rest_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CandidateRequest(
        @NotBlank(message = "Candidate name is mandatory")
        @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
        String name,
        @NotBlank(message = "Biography is mandatory")
        @Size(max = 500, message = "Bio cannot exceed 500 characters")
        String bio
) {
}
