package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "candidates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "candidate_uuid")
    private UUID candidateUUID;

    @Column(
            nullable = false,
            columnDefinition = "varchar",
            name = "candidate_fullname"
    )
    private String candidateFullName;

    @Column(
            name = "bio",
            nullable = false,
            columnDefinition = "text"
    )
    private String description;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_uuid", nullable = false)
    private Election election;

    @Column(
            name = "created_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime createdAt = LocalDateTime.now();

}
