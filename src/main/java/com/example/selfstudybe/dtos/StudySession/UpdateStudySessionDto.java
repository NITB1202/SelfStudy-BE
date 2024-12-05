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
public class UpdateStudySessionDto {
    @NotNull(message = "Id is required")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @NotNull(message = "Current stage is required")
    private Integer currentStage;

    private String musicLink;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime musicTimestamp;

    @NotNull(message = "Time left is required")
    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime timeLeft;

    private Boolean onLoop;
}
