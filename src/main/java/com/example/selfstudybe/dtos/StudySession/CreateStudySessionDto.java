package com.example.selfstudybe.dtos.StudySession;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @NotNull(message = "Total time is required")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime totalTime;

    @NotNull(message = "End time is required")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime endTime;
}
