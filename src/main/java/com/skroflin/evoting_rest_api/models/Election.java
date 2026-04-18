package com.skroflin.evoting_rest_api.models;

import com.skroflin.evoting_rest_api.enums.ElectionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "elections")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "election_uuid")
    private UUID electionUUID;

    @Column(
            nullable = false,
            name = "election_name"
    )
    private String electionName;

    @Column(
            columnDefinition = "text"
    )
    private String description;

    @Column(
            name = "election_start_time",
            columnDefinition = "timestamp"
    )
    private LocalDateTime electionStartTime;

    @Column(
            name = "election_end_time",
            columnDefinition = "timestamp"
    )
    private LocalDateTime electionEndTime;

    @Column(
            columnDefinition = "varchar",
            name = "public_key"
    )
    private String publicKey;

    @Column(
            columnDefinition = "varchar",
            name = "private_key_enc"
    )
    private String privateKeyEnc;

    @Column(name = "election_status")
    private ElectionStatus electionStatus;

    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidate> candidates = new ArrayList<>();

    @Column(
            columnDefinition = "timestamp",
            name = "created_at"
    )
    private LocalDateTime createdAt = LocalDateTime.now();
}
