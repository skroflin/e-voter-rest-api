package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "votes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "vote_uuid")
    private UUID voteUUID;

    @OneToOne
    @JoinColumn(name = "token_hash", referencedColumnName = "token_hash", nullable = false)
    private UsedToken usedToken;

    @ManyToOne
    @JoinColumn(name = "election_uuid")
    private Election election;

    @ManyToOne
    @JoinColumn(name = "candidate_uuid")
    private Candidate candidate;

    @Column(
            name = "cast_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime castAt;

    @Column(
            name = "created_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime createdAt;
}
