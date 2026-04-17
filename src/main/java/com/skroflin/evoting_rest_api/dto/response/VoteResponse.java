package com.skroflin.evoting_rest_api.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record VoteResponse(
    UUID voteId,
    LocalDateTime castAt,
    String message,
    String electionName
) { }
