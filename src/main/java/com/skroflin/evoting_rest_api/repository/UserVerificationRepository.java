package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {
}
