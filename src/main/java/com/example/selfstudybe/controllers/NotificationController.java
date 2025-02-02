package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Notification.CreateNotificationDto;
import com.example.selfstudybe.dtos.Notification.NotificationDto;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.NotificationService;
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
@RequestMapping("notification")
public class NotificationController {
    private final NotificationService notificationService;

    @PostMapping
    @Operation(summary = "Save the notification")
    @ApiResponse(responseCode = "200", description = "Save successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<NotificationDto> saveNotification(@Valid @RequestBody CreateNotificationDto notificationDto) {
        return ResponseEntity.ok(notificationService.saveNotification(notificationDto));
    }

    @GetMapping
    @Operation(summary = "Get all notifications for the user")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<NotificationDto>> getAllNotifications(@RequestParam UUID userId) {
        return ResponseEntity.ok(notificationService.getAllNotifications(userId));
    }

    @PatchMapping
    @Operation(summary = "Read the notification")
    @ApiResponse(responseCode = "200", description = "Read successfully")
    @ApiResponse(responseCode = "404", description = "Not found",content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<NotificationDto> readNotification(@RequestParam UUID notificationId) {
        return ResponseEntity.ok(notificationService.readNotification(notificationId));
    }
}
