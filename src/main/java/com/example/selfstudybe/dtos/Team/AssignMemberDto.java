package com.example.selfstudybe.dtos.Team;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class AssignMemberDto {
    @NotNull(message = "Plan id is required")
    private UUID planId;

    @NotNull(message = "Assignee's ids are required")
    private List<UUID> userIds;
}
