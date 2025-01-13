package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Team.CreateTeamDto;
import com.example.selfstudybe.dtos.Team.TeamDto;
import com.example.selfstudybe.dtos.Team.TeamMemberDto;
import com.example.selfstudybe.dtos.Team.UpdateTeamDto;
import com.example.selfstudybe.enums.TeamRole;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    @Operation(summary = "Create a new team")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TeamDto> createNewTeam(@Valid @RequestBody CreateTeamDto team) {
        return ResponseEntity.ok(teamService.createNewTeam(team));
    }

    @PostMapping(value ="image", consumes = "multipart/form-data")
    @Operation(summary = "Upload team's avatar")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    public ResponseEntity<String> uploadTeamImage(@RequestParam UUID teamId, @RequestParam MultipartFile file) throws IOException {
        return ResponseEntity.ok(teamService.uploadTeamImage(teamId,file));
    }

    @GetMapping
    @Operation(summary = "Get all teams for user")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<TeamDto>> getAllTeamsForUser(@RequestParam UUID userId) {
        return ResponseEntity.ok(teamService.getAllTeamsForUser(userId));
    }

    @GetMapping("member")
    @Operation(summary = "Get all team members based on team role")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<TeamMemberDto>> getTeamMembers(@RequestParam UUID teamId, @RequestParam(required = false) TeamRole role) {
        return ResponseEntity.ok(teamService.getAllTeamMembers(teamId, role));
    }

    @PatchMapping
    @Operation(summary = "Update team's information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<TeamDto> updateTeam(@Valid @RequestBody UpdateTeamDto team) {
        return ResponseEntity.ok(teamService.updateTeam(team));
    }

    @DeleteMapping
    @Operation(summary = "Delete the team")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> deleteTeam(@RequestParam UUID teamId) throws Exception {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok("Delete successfully");
    }
}
