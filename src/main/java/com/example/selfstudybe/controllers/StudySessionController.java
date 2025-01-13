package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.StudySession.CreateStudySessionDto;
import com.example.selfstudybe.dtos.StudySession.StudySessionDto;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.StudySessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("session")
public class StudySessionController {
    private final StudySessionService studySessionService;

    @PostMapping
    @Operation(summary = "Save the study session")
    @ApiResponse(responseCode = "200", description = "Save successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<StudySessionDto> createStudySession(@Valid @RequestBody CreateStudySessionDto studySessionDto) {
        return ResponseEntity.ok(studySessionService.createSession(studySessionDto));
    }
}
