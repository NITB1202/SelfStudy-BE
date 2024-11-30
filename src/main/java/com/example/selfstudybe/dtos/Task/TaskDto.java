package com.example.selfstudybe.dtos.Task;

import com.example.selfstudybe.enums.TaskStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class TaskDto implements Serializable {
    private UUID taskId;

    private UUID planId;

    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TaskStatus status;
}
