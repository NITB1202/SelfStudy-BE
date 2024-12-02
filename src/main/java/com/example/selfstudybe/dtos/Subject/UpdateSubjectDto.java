package com.example.selfstudybe.dtos.Subject;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class UpdateSubjectDto {
    @NotNull(message = "Subject id is required")
    private UUID id;

    private String name;
}
