package com.example.selfstudybe.dtos.Task;

import com.example.selfstudybe.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class UpdateTaskDto {
    private UUID taskId;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TaskStatus status;
}
