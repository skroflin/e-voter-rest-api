package com.skroflin.evoting_rest_api.models;

import com.skroflin.evoting_rest_api.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "admin_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "admin_uuid")
    private UUID adminUUID;

    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    private Role role;

    @Column(
            columnDefinition = "timestamp",
            name = "created_at"
    )
    private LocalDateTime createdAt = LocalDateTime.now();
}
