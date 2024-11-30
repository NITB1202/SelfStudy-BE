package com.example.selfstudybe.dtos.Task;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class CreateTaskDto {
    private UUID planId;
    private String name;
}
