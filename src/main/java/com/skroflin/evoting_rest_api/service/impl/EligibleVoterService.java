package com.skroflin.evoting_rest_api.service.impl;

import com.skroflin.evoting_rest_api.dto.request.VerificationRequest;
import com.skroflin.evoting_rest_api.exceptions.ResourceNotFoundException;
import com.skroflin.evoting_rest_api.exceptions.user.EmailAlreadyTakenException;
import com.skroflin.evoting_rest_api.exceptions.user.InvalidEmailDomainException;
import com.skroflin.evoting_rest_api.exceptions.user.InvalidPasswordException;
import com.skroflin.evoting_rest_api.exceptions.user.UserAlreadyExistsException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.InvalidVerificationCodeException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.VerificationCodeExpiredException;
import com.skroflin.evoting_rest_api.mappers.AuthMapper;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;
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

    @Override
    @Transactional
    public void verifyVoter(VerificationRequest verificationRequest) {
        EligibleVoter eligibleVoter = eligibleVoterRepository.findByEmail(verificationRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("Voter not found"));

        UserVerification userVerification = userVerificationRepository.findByEligibleVoter(eligibleVoter)
                .orElseThrow(() -> new InvalidVerificationCodeException("No verification code found"));

        validateVoter(userVerification, eligibleVoter, verificationRequest.code());
        eligibleVoter.setEnabled(true);
        eligibleVoterRepository.save(eligibleVoter);

        userVerificationRepository.delete(userVerification);
    }

    private void validateRegistration(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new IllegalArgumentException("Registration data musn't be null");
        }

        if (registerRequest.getEmail() == null || !registerRequest.getEmail().endsWith("@ffos.hr")) {
            throw new InvalidEmailDomainException("@ffos.hr domain is only allowed");
        }

        if (eligibleVoterRepository.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyTakenException("This email is already taken");
        }

        if (eligibleVoterRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("This username is already taken");
        }

        if (registerRequest.getPassword().length() < 8) {
            throw new InvalidPasswordException("The password must have at least 8 symbols");
        }
    }

    private void validateVoter(UserVerification userVerification, EligibleVoter eligibleVoter, String inputCode) {
        if (eligibleVoter.isEnabled()) {
            throw new IllegalArgumentException("Account is already verified");
        }
        if (!userVerification.getVerificationCode().equals(inputCode)) {
            throw new InvalidVerificationCodeException("Invalid verification code");
        }
        if (userVerification.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new VerificationCodeExpiredException("Verification code has expired");
        }
    }

    /*
        TODO: Add validation method for User Verification via "verification code"
     */

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
