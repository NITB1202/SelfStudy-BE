package com.example.selfstudybe.exception;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String timestamp;

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
}
