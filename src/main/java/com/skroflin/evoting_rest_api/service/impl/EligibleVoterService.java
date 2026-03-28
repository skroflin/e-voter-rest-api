package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.mappers.AuthMapper;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.dto.RegisterRequest;
import com.skroflin.evoting_rest_api.models.UserVerification;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import com.skroflin.evoting_rest_api.repository.UserVerificationRepository;
import com.skroflin.evoting_rest_api.service.AuthService;
import com.skroflin.evoting_rest_api.service.EmailService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EligibleVoterService implements AuthService {

    private final EligibleVoterRepository eligibleVoterRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserVerificationRepository userVerificationRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public String registerVoter(RegisterRequest registerRequest) {
        this.validateRegistration(registerRequest);

        EligibleVoter newVoter = authMapper.toEntity(registerRequest);
        newVoter.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        newVoter.setEnabled(false);
        newVoter.setTokenIssued(false);

        EligibleVoter savedVoter = eligibleVoterRepository.save(newVoter);
        String code = generateVerificationCode();
        saveVerificationCode(savedVoter, code);

        emailService.sendVerificationEmail(registerRequest.getEmail(), code);

        return "Verification code sent to" + " " + registerRequest.getEmail();
    }

    private void validateRegistration(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new IllegalArgumentException("Registration data musn't be null");
        }

        if (registerRequest.getEmail() == null || !registerRequest.getEmail().endsWith("@ffos.hr")) {
            throw new IllegalArgumentException("@ffos.hr domain is only allowed");
        }

        if (eligibleVoterRepository.existbyEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("This email is already taken");
        }

        if (eligibleVoterRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("This username is already taken");
        }

        if (registerRequest.getPassword().length() < 8) {
            throw new IllegalArgumentException("The password must have at least 8 symbols");
        }
    }

    private String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    private void saveVerificationCode(EligibleVoter eligibleVoter, String code) {
        UserVerification userVerification = new UserVerification();
        userVerification.setEligibleVoter(eligibleVoter);
        userVerification.setVerificationCode(code);
        userVerification.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        userVerificationRepository.save(userVerification);
    }
}
