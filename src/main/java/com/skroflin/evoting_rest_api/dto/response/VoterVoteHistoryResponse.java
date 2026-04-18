package com.skroflin.evoting_rest_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record VoterVoteHistoryResponse(
        UUID electionId,
        String electionName,
        LocalDateTime votedAt
) { }
