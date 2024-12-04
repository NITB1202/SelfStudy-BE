package com.example.selfstudybe.dtos.Subject;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class CreateTeamSubjectDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotNull(message = "Creator id is required")
    private UUID creatorId;

    @NotBlank(message = "Name is required")
    private String name;
}
