package com.example.selfstudybe.dtos.Notification;

import com.example.selfstudybe.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class NotificationDto {
    private UUID id;

    private UUID userId;

    private UUID planId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private NotificationType type;

    private Boolean read;

}
