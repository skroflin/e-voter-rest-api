package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ElectionRepository extends JpaRepository<Election, UUID> {

    boolean existsByElectionName(String electionName);
}
