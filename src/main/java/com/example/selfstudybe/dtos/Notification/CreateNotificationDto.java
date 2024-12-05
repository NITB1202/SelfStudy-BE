package com.example.selfstudybe.dtos.Notification;

import com.example.selfstudybe.enums.NotificationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class CreateNotificationDto {
    @NotNull(message = "User id is required")
    private UUID userId;

    @NotNull(message = "Plan id is required")
    private UUID planId;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Notification type is required")
    private NotificationType type;
}
