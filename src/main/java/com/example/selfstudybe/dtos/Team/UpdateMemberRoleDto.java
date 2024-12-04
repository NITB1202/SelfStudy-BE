package com.example.selfstudybe.dtos.Team;

import com.example.selfstudybe.enums.TeamRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class UpdateMemberRoleDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Role is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TeamRole role;
}
