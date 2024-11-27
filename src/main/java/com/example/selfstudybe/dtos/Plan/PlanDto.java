package com.example.selfstudybe.dtos.Plan;

import com.example.selfstudybe.enums.PlanStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record PlanDto(UUID id, String name, LocalDate startDate, LocalDate endDate, LocalTime notifyBefore,
                      PlanStatus status) implements Serializable {
}