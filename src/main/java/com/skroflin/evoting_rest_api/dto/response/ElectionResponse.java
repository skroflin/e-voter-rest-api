package com.skroflin.evoting_rest_api.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ElectionResponse(
        UUID id,
        String title,
        String description,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<CandidateResponse> candidates,
        boolean isActive
) {
}
