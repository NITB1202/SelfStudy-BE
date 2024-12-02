package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Subject.CreateUserSubjectDto;
import com.example.selfstudybe.dtos.Subject.SubjectDto;
import com.example.selfstudybe.dtos.Subject.UpdateSubjectDto;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("v1/subject")
public class SubjectController {
    private final SubjectService subjectService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new user's subject")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<SubjectDto> createUserSubject(@Valid @RequestBody CreateUserSubjectDto subjectDto) {
        return ResponseEntity.ok(subjectService.createUserSubject(subjectDto));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get all of the user's personal subjects")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<List<SubjectDto>> getAllUserSubjects(@RequestParam UUID userId) {
        return ResponseEntity.ok(subjectService.getAllUserSubjects(userId));
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update subject's information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<SubjectDto> updateSubject(@Valid @RequestBody UpdateSubjectDto subject) {
        return ResponseEntity.ok(subjectService.updateSubject(subject));
    }

    @PostMapping(value = "image/{id}", consumes = "multipart/form-data")
    @Operation(summary = "Upload subject's image")
    @ApiResponse(responseCode = "200", description = "Upload successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<String> uploadSubjectImage(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(subjectService.uploadSubjectImage(id,file));
    }

    @DeleteMapping
    @Operation(summary = "Delete the subject")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    public ResponseEntity<String> deleteSubject(@RequestParam UUID id) throws IOException {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Delete successfully");
    }





}
