package com.example.selfstudybe.dtos.StudySession;

import com.example.selfstudybe.enums.SessionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class CreateStudySessionDto {
    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Focus time is required")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime focusTime;

    @NotNull(message = "Break time is required")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime breakTime;

    @NotNull(message = "Total stages are required")
    private Integer totalStage;

    private String musicLink;

    private Boolean onLoop;
}
