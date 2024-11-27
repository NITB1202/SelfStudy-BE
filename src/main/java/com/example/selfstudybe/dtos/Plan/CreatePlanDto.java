package com.example.selfstudybe.dtos.Plan;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class CreatePlanDto {
    @NotBlank(message = "Name is required")
    String name;

    @NotNull(message = "Start date is required")
    LocalDate startDate;

    @NotNull(message = "End date is required")
    LocalDate endDate;

    LocalTime notifyBefore;
}
