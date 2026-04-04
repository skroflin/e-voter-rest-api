package com.skroflin.evoting_rest_api.controller;

import com.skroflin.evoting_rest_api.dto.request.LoginRequest;
import com.skroflin.evoting_rest_api.dto.request.RegisterRequest;
import com.skroflin.evoting_rest_api.dto.request.VerificationRequest;
import com.skroflin.evoting_rest_api.dto.response.LoginResponse;
import com.skroflin.evoting_rest_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest) {
        String response = authService.registerVoter(registerRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody VerificationRequest verificationRequest) {
        authService.verifyVoter(verificationRequest);
        return ResponseEntity.ok("Verification successful, you can now log in.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }
}
