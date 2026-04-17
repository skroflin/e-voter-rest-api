package com.skroflin.evoting_rest_api.service;

import com.skroflin.evoting_rest_api.models.Vote;

public interface CryptoService {

    String generateSignature(Vote vote);
    boolean verifySignature(Vote vote, String signature);
}
