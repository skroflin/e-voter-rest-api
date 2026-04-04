package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.Election;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ElectionRepository extends JpaRepository<Election, UUID> {
}
