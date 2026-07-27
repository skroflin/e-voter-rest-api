package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.models.IssuedToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IssuedTokenRepository extends JpaRepository<IssuedToken, UUID> {

    boolean existsByVoter(EligibleVoter voter);
}
