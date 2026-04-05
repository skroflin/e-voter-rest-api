package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.dto.request.ElectionRequest;
import com.skroflin.evoting_rest_api.dto.response.ElectionResponse;

public interface ElectionService {

    public ElectionResponse createElection(ElectionRequest electionRequest);
}
