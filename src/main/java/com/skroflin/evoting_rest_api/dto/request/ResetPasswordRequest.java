package com.skroflin.evoting_rest_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(
        @NotBlank(message = "Verification code is required")
        String code,
        @NotBlank(message = "New password is required")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String newPassword,
        @NotBlank(message = "Password confirmation is required")
        String confirmPassword
) {
}
