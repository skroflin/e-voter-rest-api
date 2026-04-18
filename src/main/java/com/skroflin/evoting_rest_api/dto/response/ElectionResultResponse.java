package com.skroflin.evoting_rest_api.dto.response;

import java.util.List;

public record ElectionResultResponse(
        String electionName,
        boolean isClosed,
        List<CandidateResultResponse> results,
        String message
) { }
