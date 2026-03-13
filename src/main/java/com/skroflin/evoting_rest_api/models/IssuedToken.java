package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "issued_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IssuedToken {

    @Id
    @Column(name = "token_uuid")
    private UUID tokenUUID;

    @OneToOne
    @JoinColumn(name = "voter_uuid")
    private EligibleVoter voter;

    @Column(
            name = "issued_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime issuedAt;

    @Column(
            name = "created_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime createdAt;
}
