package com.example.selfstudybe.dtos.User;

import com.example.selfstudybe.enums.Role;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@Getter @Setter
public class CreateUserDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Username is required")
    @Size(max = 15, message = "Username must be fewer than 15 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Password must not contain special characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;
}