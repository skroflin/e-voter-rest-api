package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.EligibleVoter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EligibleVoterRepository extends JpaRepository<EligibleVoter, UUID> {

    Optional<EligibleVoter> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<EligibleVoter> findByEmail(String email);
}
