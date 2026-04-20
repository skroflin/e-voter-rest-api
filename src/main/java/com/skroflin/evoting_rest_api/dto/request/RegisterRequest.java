package com.skroflin.evoting_rest_api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "First name is necessary")
    private String firstName;
    @NotBlank(message = "Last name is necessary")
    private String lastName;
    @NotBlank(message = "Email is necessary")
    @Email
    private String email;
    @NotBlank(message = "Username is necessary")
    private String username;
    @NotBlank(message = "Password can't have less than 8 symbols")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 150 characters")
    private String password;
}
