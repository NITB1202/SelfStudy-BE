package com.example.selfstudybe.dtos.StudySession;

import com.example.selfstudybe.enums.SessionStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class StudySessionDto {
    private UUID id;

    private UUID userId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreate;

    @Enumerated(EnumType.STRING)
    private SessionStatus status;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime totalTime;

    @JsonFormat(pattern = "HH:mm:ss")
    @Schema(type = "string", format = "HH:mm:ss", example = "00:00:00")
    private LocalTime endTime;
}
