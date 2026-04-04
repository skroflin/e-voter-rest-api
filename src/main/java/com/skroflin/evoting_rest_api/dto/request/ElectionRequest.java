package com.skroflin.evoting_rest_api.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record ElectionRequest(
        @NotBlank(message = "Title is mandatory")
        @Size(min = 3, max = 10)
        String title,

        @NotBlank(message = "Description is mandatory")
        String description,

        @NotNull(message = "Start date is mandatory")
        @Future(message = "Start date must be in the future")
        LocalDateTime startDate,

        @NotNull(message = "End date is mandatory")
        @Future(message = "End date must be in the future")
        LocalDateTime endDate,

        @NotEmpty(message = "At least two candidates are required")
        List<CandidateRequest> candidates
) {
}
