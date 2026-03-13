package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "eligible_voters")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EligibleVoter {

    @Id
    private UUID voterUUID;

    @Column(
            columnDefinition = "varchar",
            name = "ldap_UUID_hash"
    )
    private String ldapUUIDHash;

    @Column(name = "is_token_issued")
    private boolean tokenIssued = false;

    @Column(
            name = "token_issued_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime tokenIssuedAt;

    @Column(
            name = "created_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime createdAt = LocalDateTime.now();
}
