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
@Table(name = "admin_users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "admin_uuid")
    private UUID adminUUID;

    @Column(unique = true)
    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    private Role role;

    @Column(
            columnDefinition = "timestamp",
            name = "created_at"
    )
    private LocalDateTime createdAt = LocalDateTime.now();

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
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public @Nullable String getPassword() {
        return this.passwordHash;
    }
}
