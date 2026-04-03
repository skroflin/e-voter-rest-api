package com.skroflin.evoting_rest_api.service.validation;

import com.skroflin.evoting_rest_api.models.EligibleVoter;
import com.skroflin.evoting_rest_api.models.UserVerification;
import com.skroflin.evoting_rest_api.repository.UserVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class VerificationHelper {

    private final UserVerificationRepository userVerificationRepository;

    public String generateVerificationCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }

    public void saveVerificationCode(EligibleVoter eligibleVoter, String code) {
        UserVerification userVerification = new UserVerification();
        userVerification.setEligibleVoter(eligibleVoter);
        userVerification.setVerificationCode(code);
        userVerification.setExpiryDate(LocalDateTime.now().plusMinutes(15));
        userVerificationRepository.save(userVerification);
    }
}
