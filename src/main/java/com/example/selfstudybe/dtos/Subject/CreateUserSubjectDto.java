package com.example.selfstudybe.dtos.Subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class CreateUserSubjectDto {
    @NotNull(message = "User id is required")
    private UUID userId;

    @NotBlank(message = "Name is required")
    private String name;
}
