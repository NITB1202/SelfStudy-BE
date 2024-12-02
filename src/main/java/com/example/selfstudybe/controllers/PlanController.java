package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Plan.CreateUserPlanDto;
import com.example.selfstudybe.dtos.Plan.PlanDto;
import com.example.selfstudybe.dtos.Plan.UpdatePlanDto;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("v1/plan")
public class PlanController {
    private final PlanService planService;

    @PostMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create new user's plan")
    @ApiResponse(responseCode = "200", description = "Create successfully", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = PlanDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "NotifyBefore is optional")
    public ResponseEntity<PlanDto> createUserPlan(@Valid @RequestBody CreateUserPlanDto plan, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new CustomBadRequestException(String.join(", ", errors));
        }

        PlanDto response = planService.createUserPlan(plan);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value ="date", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user's plans on a specific date")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<PlanDto>> getPlansOnDate(@RequestParam UUID id, @RequestParam LocalDate date) {
        return ResponseEntity.ok(planService.getUserPlansOnDate(id,date));
    }

    @GetMapping(value = "missed", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user's missed plans")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<PlanDto>> getUserMissedPlan(@RequestParam UUID id) {
        return ResponseEntity.ok(planService.getUserMissedPlans(id));
    }

    @PatchMapping(value = "user", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update plan's information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are optional except planId")
    public ResponseEntity<PlanDto>  updatePlan(@Valid @RequestBody UpdatePlanDto plan) {
        return ResponseEntity.ok(planService.updatePlan(plan));
    }

    @DeleteMapping("user")
    @Operation(summary = "Delete the plan")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String>  deleteUserPlan(@RequestParam UUID id) {
        planService.deletePlan(id);
        return ResponseEntity.ok("Delete successfully");
    }
}
