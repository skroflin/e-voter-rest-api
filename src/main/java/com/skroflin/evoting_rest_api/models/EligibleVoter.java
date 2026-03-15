package com.skroflin.evoting_rest_api.models;

import com.skroflin.evoting_rest_api.enums.Role;
import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "voter_uuid")
    private UUID voterUUID;

    @Column(name = "first_name", columnDefinition = "varchar")
    private String firstName;

    @Column(name = "last_name", columnDefinition = "varchar")
    private String lastName;

    @Column(columnDefinition = "varchar")
    private String username;

    @Column(
            columnDefinition = "varchar",
            name = "password_hash"
    )
    private String passwordHash;

    @Column(name = "is_token_issued")
    private boolean tokenIssued = false;

    @Column(
            name = "token_issued_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime tokenIssuedAt;

    private Role role;

    @Column(
            name = "created_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime createdAt = LocalDateTime.now();
}
