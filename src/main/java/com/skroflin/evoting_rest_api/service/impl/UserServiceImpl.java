package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.response.UserResponse;
import com.skroflin.evoting_rest_api.exceptions.user.UnknownUserException;
import com.skroflin.evoting_rest_api.models.AdminUser;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.repository.AdminRepository;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import com.skroflin.evoting_rest_api.service.UserServce;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserServce {

    private final EligibleVoterRepository voterRepository;
    private final AdminRepository adminRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUserProfile(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnknownUserException("User not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof EligibleVoter voterPrincipal) {
            EligibleVoter voter = voterRepository.findById(voterPrincipal.getVoterUUID())
                    .orElseThrow(() -> new UnknownUserException("Voter not found."));

            return UserResponse.builder()
                    .username(voter.getUsername())
                    .role(voter.getRole())
                    .createdAt(voter.getCreatedAt())
                    .firstName(voter.getFirstName())
                    .lastName(voter.getLastName())
                    .email(voter.getEmail())
                    .enabled(voter.isEnabled())
                    .tokenIssued(voter.isTokenIssued())
                    .build();
        }

        if (principal instanceof AdminUser adminPrincipal) {
            AdminUser admin = adminRepository.findById(adminPrincipal.getAdminUUID())
                    .orElseThrow(() -> new UnknownUserException("Admin not found."));

            return UserResponse.builder()
                    .username(admin.getUsername())
                    .role(admin.getRole())
                    .createdAt(admin.getCreatedAt())
                    .build();
        }

        throw new UnknownUserException("Unknown user type.");
    }
}