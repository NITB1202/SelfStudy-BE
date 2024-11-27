package com.example.selfstudybe.dtos.User;

import com.example.selfstudybe.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class UpdateUserDto implements Serializable {
    @NotNull(message = "User id is required")
    UUID id;

    @Email(message = "Invalid email format")
    private final String email;

    @Size(max = 15, message = "Username must be fewer than 15 characters")
    private final String username;

    @Size(min = 6, max = 15, message = "Password must be between 6 and 15 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]*$", message = "Password must not contain special characters")
    private final String password;

    private final String avatarLink;

    private final Role role;
}