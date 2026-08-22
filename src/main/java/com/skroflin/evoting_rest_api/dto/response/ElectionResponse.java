package com.skroflin.evoting_rest_api.dto.response;

import com.skroflin.evoting_rest_api.enums.ElectionStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ElectionResponse(
        UUID id,
        String title,
        String description,
        ElectionStatus status,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<CandidateResponse> candidates,
        boolean isActive
) {
}
