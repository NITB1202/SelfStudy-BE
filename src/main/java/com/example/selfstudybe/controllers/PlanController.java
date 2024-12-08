package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Plan.*;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new user's plan")
    @ApiResponse(responseCode = "200", description = "Create successfully", content =
        @Content(mediaType = "application/json", schema = @Schema(implementation = PlanDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "NotifyBefore is optional")
    public ResponseEntity<PlanDto> createUserPlan(@Valid @RequestBody CreateUserPlanDto plan) {
        return ResponseEntity.ok(planService.createUserPlan(plan));
    }

    @PostMapping(value = "team", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new team's plan")
    public ResponseEntity<PlanDto> createTeamPlan(@Valid @RequestBody CreateTeamPlanDto plan) {
        return ResponseEntity.ok(planService.createTeamPlan(plan));
    }

    @GetMapping(value ="date", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user's plans on a specific date")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<PlanDto>> getPlansOnDate(@RequestParam UUID id, @RequestParam LocalDate date) {
        return ResponseEntity.ok(planService.getUserPlansOnDate(id,date));
    }

    @GetMapping(value = "team/date", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get team's plans in a specific date")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<TeamPlanDto>> getTeamPlansOnDate(@RequestParam UUID userId, @RequestParam UUID teamId, @RequestParam LocalDate date) {
        return ResponseEntity.ok(planService.getTeamPlansOnDate(userId,teamId,date));
    }

    @GetMapping("month")
    @Operation(summary = "Get the dates when the user has deadlines in month")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<String>> getDatesHasDeadlineInMonth(@RequestParam UUID userId,
                                                                   @Range(min = 1, max = 12, message = "Month must between 1 and 12" ) @RequestParam int month,
                                                                   @Positive(message = "Year can't be less than 0") @RequestParam int year) {
        List<LocalDate> dates = planService.getDatesHasDeadlineInMonth(userId, month, year);
        List<String> formattedDates = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(formattedDates);
    }

    @GetMapping("month/team")
    @Operation(summary = "Get the dates when the team has deadlines in month")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<String>> getDatesTeamHasDeadlineInMonth(@RequestParam UUID teamId,
                                                                       @Range(min = 1, max = 12, message = "Month must between 1 and 12" ) @RequestParam int month,
                                                                       @Positive(message = "Year can't be less than 0") @RequestParam int year) {
        List<LocalDate> dates = planService.getDatesTeamHasDeadlineInMonth(teamId, month, year);
        List<String> formattedDates = dates.stream()
                .map(LocalDate::toString)
                .collect(Collectors.toList());
        return ResponseEntity.ok(formattedDates);
    }

    @GetMapping(value = "missed", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get user's missed plans")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<PlanDto>> getUserMissedPlan(@RequestParam UUID id) {
        return ResponseEntity.ok(planService.getUserMissedPlans(id));
    }

    @GetMapping(value = "team/missed", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get team's missed plans")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<List<PlanDto>> getTeamMissedPlans(@RequestParam UUID id) {
        return ResponseEntity.ok(planService.getTeamMissedPlans(id));
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
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

    @DeleteMapping
    @Operation(summary = "Delete the plan")
    @ApiResponse(responseCode = "200", description = "Delete successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
        @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public ResponseEntity<String>  deleteUserPlan(@RequestParam UUID id) {
        planService.deletePlan(id);
        return ResponseEntity.ok("Delete successfully");
    }
}
