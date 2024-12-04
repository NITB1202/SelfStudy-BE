package com.example.selfstudybe.dtos.Team;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class UpdateTeamDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    private String name;
}
