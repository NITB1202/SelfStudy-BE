package com.example.selfstudybe.dtos.Plan;

import com.example.selfstudybe.validators.ValidDateRange;
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
@ValidDateRange
public class UpdatePlanDto {
    @NotNull(message = "Plan id is required")
    private UUID planId;

    private String name;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime notifyBefore;
}
