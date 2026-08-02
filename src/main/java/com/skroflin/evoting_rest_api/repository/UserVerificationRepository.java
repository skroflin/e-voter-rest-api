package com.skroflin.evoting_rest_api.repository;

import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.models.UserVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserVerificationRepository extends JpaRepository<UserVerification, UUID> {

    Optional<UserVerification> findByEligibleVoter(EligibleVoter eligibleVoter);
    Optional<UserVerification> findByVerificationCode(String verificationCode);
    void deleteByEligibleVoter(EligibleVoter eligibleVoter);
}
