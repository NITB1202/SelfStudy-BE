package com.example.selfstudybe.dtos.Team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class CreateTeamDto {
    @NotNull(message = "Creator id is required")
    private UUID creatorId;

    @NotBlank(message = "Name is required")
    private String name;
}
