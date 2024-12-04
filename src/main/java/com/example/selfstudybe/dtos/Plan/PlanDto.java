package com.example.selfstudybe.dtos.Plan;

import com.example.selfstudybe.enums.PlanStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class PlanDto implements Serializable {
    private UUID id;

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime notifyBefore;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private PlanStatus status;

    private double process;

    private boolean personal;
}