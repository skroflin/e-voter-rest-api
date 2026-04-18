package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "election_participations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ElectionParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "election_participation_uuid")
    private UUID electionParticipationUUID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_uuid", nullable = false)
    private EligibleVoter eligibleVoter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "election_uuid", nullable = false)
    private Election election;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
}
