package com.skroflin.evoting_rest_api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "used_tokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsedToken {

    @Id
    @Column(
            name = "token_hash",
            columnDefinition = "varchar"
    )
    private String tokenHash;

    @Column(
            name = "used_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime usedAt;

    @Column(
            name = "created_at",
            columnDefinition = "timestamp"
    )
    private LocalDateTime createdAt;
}
