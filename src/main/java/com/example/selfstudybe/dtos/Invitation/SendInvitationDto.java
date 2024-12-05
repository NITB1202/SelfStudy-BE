package com.example.selfstudybe.dtos.Invitation;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class SendInvitationDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotNull(message = "User id is required")
    private UUID userId;
}
