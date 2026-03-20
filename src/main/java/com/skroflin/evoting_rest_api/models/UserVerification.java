package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_verification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "verification_user_uuid")
    private UUID userVerificationUUID;

    @OneToOne
    @JoinColumn(name = "voter_uuid", referencedColumnName = "voter_uuid")
    private EligibleVoter eligibleVoter;

    @Column(name = "verification_code", nullable = false)
    private String verificationCode;

    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @Column(name = "created_at", columnDefinition = "timestamp")
    private LocalDateTime createdAt = LocalDateTime.now();
}
