package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Team.*;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.TeamMemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("member")
public class TeamMemberController {
    private final TeamMemberService teamMemberService;

    @PostMapping
    @Operation(summary = "Add members to the team")
    @ApiResponse(responseCode = "200", description = "Add successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> addTeamMember(@Valid @RequestBody AddTeamMemberDto teamMemberDto) {
        teamMemberService.addTeamMember(teamMemberDto);
        return ResponseEntity.ok("Add successfully");
    }

    @GetMapping
    @Operation(summary = "Get user role in team")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> getUserRole(UUID teamId, UUID userId) {
        return ResponseEntity.ok(teamMemberService.getUserRole(teamId, userId));
    }

    @GetMapping("plan")
    @Operation(summary = "Get plan assignees")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<TeamMemberDto>> getPlanAssignees(@RequestParam UUID planId) {
        return ResponseEntity.ok(teamMemberService.getPlanAssignees(planId));
    }

    @PutMapping
    @Operation(summary = "Update team member's role")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> updateRole(@Valid @RequestBody UpdateMemberRoleDto updateMemberRoleDto) {
        teamMemberService.updateTeamMemberRole(updateMemberRoleDto);
        return ResponseEntity.ok("Update successfully");
    }

    @DeleteMapping
    @Operation(summary = "Remove team members")
    @ApiResponse(responseCode = "200", description = "Remove successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> removeTeamMember(@Valid @RequestBody RemoveTeamMemberDto removeTeamMemberDto) {
        teamMemberService.removeTeamMember(removeTeamMemberDto);
        return ResponseEntity.ok("Remove successfully");
    }

    @DeleteMapping("plan")
    @Operation(summary = "Remove assignee from the team's plan")
    @ApiResponse(responseCode = "200", description = "Remove successfully")
    public ResponseEntity<String> removeAssignee(@RequestParam UUID planId, @RequestParam UUID userId) {
        teamMemberService.removeAssignee(planId, userId);
        return ResponseEntity.ok("Remove successfully");
    }

    @PostMapping("plan")
    @Operation(summary = "Assign members to a plan")
    @ApiResponse(responseCode = "200", description = "Assign successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> assignMembers(@Valid @RequestBody AssignMemberDto assignMemberDto) {
        teamMemberService.assignTeamMember(assignMemberDto);
        return ResponseEntity.ok("Assign successfully");
    }
}
