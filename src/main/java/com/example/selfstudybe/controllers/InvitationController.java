package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Invitation.InvitationDto;
import com.example.selfstudybe.dtos.Invitation.SendInvitationDto;
import com.example.selfstudybe.enums.Response;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.InvitationService;
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
@RequestMapping("v1/invitation")
public class InvitationController {
    private final InvitationService invitationService;

    @PostMapping
    @Operation(summary = "Send the invitation")
    @ApiResponse(responseCode = "200", description = "Send successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<InvitationDto> sendInvitation(@Valid @RequestBody SendInvitationDto invitationDto) {
        return ResponseEntity.ok(invitationService.sendInvitation(invitationDto));
    }

    @GetMapping
    @Operation(summary = "Get all invitations for the user")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<InvitationDto>> getAllInvitations(@RequestParam UUID userId) {
        return ResponseEntity.ok(invitationService.getAllInvitations(userId));
    }

    @PatchMapping
    @Operation(summary = "Respond to the invitation")
    @ApiResponse(responseCode = "200", description = "Respond successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<InvitationDto> respondToInvitation(@RequestParam UUID invitationId, @RequestParam Response response) {
        return ResponseEntity.ok(invitationService.respondInvitation(invitationId, response));
    }
}
