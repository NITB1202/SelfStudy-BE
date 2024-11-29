package com.example.selfstudybe.dtos.Plan;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateUserPlanDto {
    @NotNull(message = "User id is required")
    private UUID userId;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime notifyBefore;
}