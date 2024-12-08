package com.example.selfstudybe.dtos.Plan;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class CreateTeamPlanDto {
    @NotNull(message = "Team id is required")
    private UUID teamId;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Start date is required")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    private LocalDateTime endDate;

    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime notifyBefore;
}
