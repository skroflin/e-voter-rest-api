package com.skroflin.evoting_rest_api.service.validation;

import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;
import com.skroflin.evoting_rest_api.exceptions.user.EmailAlreadyTakenException;
import com.skroflin.evoting_rest_api.exceptions.user.InvalidEmailDomainException;
import com.skroflin.evoting_rest_api.exceptions.user.InvalidPasswordException;
import com.skroflin.evoting_rest_api.exceptions.user.UserAlreadyExistsException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.InvalidVerificationCodeException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.VerificationCodeExpiredException;
import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.models.UserVerification;
import com.skroflin.evoting_rest_api.repository.EligibleVoterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class VoterValidator {

    private final EligibleVoterRepository eligibleVoterRepository;

    public void validateRegistration(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            throw new IllegalArgumentException("Registration data mustn't be null");
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

    public void validateVoter(UserVerification userVerification, EligibleVoter eligibleVoter, String inputCode) {
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
}
