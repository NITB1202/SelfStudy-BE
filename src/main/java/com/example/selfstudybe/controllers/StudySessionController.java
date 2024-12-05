package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.StudySession.CreateStudySessionDto;
import com.example.selfstudybe.dtos.StudySession.StudySessionDto;
import com.example.selfstudybe.dtos.StudySession.UpdateStudySessionDto;
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

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("v1/session")
public class StudySessionController {
    private final StudySessionService studySessionService;

    @PostMapping
    @Operation(summary = "Create a new study session")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "MusicLink and onLoop are optional")
    public ResponseEntity<StudySessionDto> createStudySession(@Valid @RequestBody CreateStudySessionDto studySessionDto) {
        return ResponseEntity.ok(studySessionService.createSession(studySessionDto));
    }

    @GetMapping
    @Operation(summary = "Load the study session")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<StudySessionDto> getRecentStudySession(@RequestParam UUID userId) {
        return ResponseEntity.ok(studySessionService.getStudySession(userId));
    }

    @PatchMapping
    @Operation(summary = "Save the study session")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "MusicLink, musicTimeStamp and onLoop are optional")
    public ResponseEntity<StudySessionDto> updateStudySession(@Valid @RequestBody UpdateStudySessionDto studySessionDto) {
        return ResponseEntity.ok(studySessionService.updateSession(studySessionDto));
    }
}
