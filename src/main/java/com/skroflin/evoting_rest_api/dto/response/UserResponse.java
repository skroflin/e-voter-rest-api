package com.skroflin.evoting_rest_api.dto.response;

import com.skroflin.evoting_rest_api.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private String username;
    private Role role;
    private LocalDateTime createdAt;

    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private Boolean tokenIssued;
}
