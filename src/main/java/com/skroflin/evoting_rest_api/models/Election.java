package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDate;
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
    private UUID electionUUID;

    @Column(nullable = false)
    private String electionName;
    @Column(columnDefinition = "TEXT")
    private String description;
    private LocalDate electionStartTime;
    private LocalDate electionEndTime;
    @Column(columnDefinition = "TEXT")
    private String publicKey;
    @Column(columnDefinition = "TEXT")
    private String privateKeyEnc;
    @Column(name = "election_status")
    private int statusValue;
    @OneToMany(mappedBy = "election", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidate> candidates = new ArrayList<>();
    private Timestamp createdAt;
}
