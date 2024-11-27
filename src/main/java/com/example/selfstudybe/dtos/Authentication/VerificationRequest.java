package com.example.selfstudybe.dtos.Authentication;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VerificationRequest {
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Code is required")
    private String code;
}
