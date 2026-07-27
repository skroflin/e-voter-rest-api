package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.config.jwt.JwtService;
import com.skroflin.evoting_rest_api.dto.request.LoginRequest;
import com.skroflin.evoting_rest_api.dto.request.VerificationRequest;
import com.skroflin.evoting_rest_api.dto.response.LoginResponse;
import com.skroflin.evoting_rest_api.enums.Role;
import com.skroflin.evoting_rest_api.exceptions.ResourceNotFoundException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.InvalidVerificationCodeException;
import com.skroflin.evoting_rest_api.mappers.AuthMapper;
import com.skroflin.evoting_rest_api.models.AdminUser;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;
import com.skroflin.evoting_rest_api.models.UserVerification;
import com.skroflin.evoting_rest_api.repository.AdminRepository;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import com.skroflin.evoting_rest_api.repository.UserVerificationRepository;
import com.skroflin.evoting_rest_api.service.AuthService;
import com.skroflin.evoting_rest_api.service.EmailService;
import com.skroflin.evoting_rest_api.service.validation.VerificationHelper;
import com.skroflin.evoting_rest_api.service.validation.VoterValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EligibleVoterService implements AuthService {

    private final EligibleVoterRepository eligibleVoterRepository;
    private final AdminRepository adminRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationRepository userVerificationRepository;
    private final EmailService emailService;
    private final VoterValidator voterValidator;
    private final VerificationHelper verificationHelper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public String registerVoter(RegisterRequest registerRequest) {
        voterValidator.validateRegistration(registerRequest);

        EligibleVoter newVoter = authMapper.toEntity(registerRequest);
        newVoter.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        newVoter.setRole(Role.ROLE_VOTER);
        newVoter.setEnabled(false);
        newVoter.setTokenIssued(false);

        EligibleVoter savedVoter = eligibleVoterRepository.save(newVoter);

        String code = verificationHelper.generateVerificationCode();
        verificationHelper.saveVerificationCode(savedVoter, code);

        emailService.sendVerificationEmail(registerRequest.getEmail(), code);

        return "Verification code sent to" + " " + registerRequest.getEmail();
    }

    @Override
    @Transactional
    public void verifyVoter(VerificationRequest verificationRequest) {
        EligibleVoter eligibleVoter = eligibleVoterRepository.findByEmail(verificationRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));

        UserVerification userVerification = userVerificationRepository.findByEligibleVoter(eligibleVoter)
                .orElseThrow(() -> new InvalidVerificationCodeException("No verification code found"));

        voterValidator.validateVoter(userVerification, eligibleVoter, verificationRequest.code());
        eligibleVoter.setEnabled(true);
        eligibleVoterRepository.save(eligibleVoter);

        userVerificationRepository.delete(userVerification);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserDetails user = eligibleVoterRepository.findByUsername(request.username())
                .map(voter -> (UserDetails) voter)
                .or(() -> eligibleVoterRepository.findByEmail(request.username()).map(voter -> (UserDetails) voter))
                .or(() -> adminRepository.findByUsername(request.username()).map(admin -> (UserDetails) admin))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + request.username()));

        String jwtToken = jwtService.generateToken(user);

        String roleDisplayName = Role.UNKNOWN.getName();
        if (user instanceof AdminUser admin) {
            roleDisplayName = admin.getRole().getName();
        } else if (user instanceof EligibleVoter voter) {
            roleDisplayName = voter.getRole().getName();
        }

        return LoginResponse.builder()
                .token(jwtToken)
                .type("bearer")
                .expiresIn(jwtService.getExpirationTime())
                .username(user.getUsername())
                .role(roleDisplayName)
                .build();
    }
}
