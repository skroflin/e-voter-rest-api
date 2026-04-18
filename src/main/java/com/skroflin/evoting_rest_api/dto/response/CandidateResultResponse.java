package com.skroflin.evoting_rest_api.dto.response;

import java.util.UUID;

public record CandidateResultResponse(
    String fullName,
    UUID candidateUUID,
    Long voteCount
) { }
