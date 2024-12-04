package com.example.selfstudybe.dtos.Team;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class RemoveTeamMemberDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotNull(message = "Member ids are required")
    private List<UUID> memberIds;
}
