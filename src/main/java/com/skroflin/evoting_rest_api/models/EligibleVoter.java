package com.skroflin.evoting_rest_api.models;

import com.skroflin.evoting_rest_api.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "eligible_voters")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EligibleVoter implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "voter_uuid")
    private UUID voterUUID;

    @Column(name = "first_name", columnDefinition = "varchar", nullable = false)
    private String firstName;

    @Column(name = "last_name", columnDefinition = "varchar", nullable = false)
    private String lastName;

    @Column(columnDefinition = "varchar", unique = true, nullable = false)
    private String email;

    @Column(name = "is_enabled")
    private boolean enabled;

    @Column(columnDefinition = "varchar", unique = true, nullable = false)
    private String username;

    @Column(
            columnDefinition = "varchar",
            name = "password_hash",
            nullable = false
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

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
