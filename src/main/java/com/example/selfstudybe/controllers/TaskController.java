package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Task.CreateTaskDto;
import com.example.selfstudybe.dtos.Task.TaskDto;
import com.example.selfstudybe.dtos.Task.UpdateTaskDto;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.TaskService;
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
@RequestMapping("v1/task")
@AllArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping
    @Operation(summary = "Create a new task")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are required")
    public ResponseEntity<TaskDto> createTask(@Valid @RequestBody CreateTaskDto taskDto) {
        return ResponseEntity.ok(taskService.createTask(taskDto));
    }

    @GetMapping
    @Operation(summary = "Get all tasks belong to the plan")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<TaskDto>> getTaskByPlan(@RequestParam UUID planId) {
        return ResponseEntity.ok(taskService.getAllTasksForPlan(planId));
    }

    @PatchMapping
    @Operation(summary = "Update task's information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are optional except taskId")
    public ResponseEntity<TaskDto> updateTask(@Valid @RequestBody UpdateTaskDto taskDto) {
        return ResponseEntity.ok(taskService.updateTask(taskDto));
    }

    @DeleteMapping
    @Operation(summary = "Delete a task")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
    @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String> deleteTask(@RequestParam UUID taskId) {
        taskService.deleteTask(taskId);
        return ResponseEntity.ok("Delete successfully");
    }
}
