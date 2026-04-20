package com.skroflin.evoting_rest_api.dto.request;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.List;

public record ElectionRequest(
        @NotBlank(message = "Title is mandatory")
        @Size(min = 100, max = 500)
        String title,

        @NotBlank(message = "Description is mandatory")
        @Size(min = 500, max = 2000)
        String description,

        @NotNull(message = "Start date is mandatory")
        @Future(message = "Start date must be in the future")
        LocalDateTime startDate,

        @NotNull(message = "End date is mandatory")
        @Future(message = "End date must be in the future")
        LocalDateTime endDate,

        @NotEmpty(message = "At least two candidates are required")
        @Size(min = 2, message = "At least two candidates are required")
        List<CandidateRequest> candidates
) {
}
