package com.skroflin.evoting_rest_api.dto.request;

import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import jakarta.validation.constraints.NotNull;

public record ElectionStatusUpdateRequest(
        @NotNull(message = "Election status is mandatory")
        ElectionStatus electionStatus
) {
}
