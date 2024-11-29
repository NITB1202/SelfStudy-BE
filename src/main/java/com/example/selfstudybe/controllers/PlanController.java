package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Plan.CreateUserPlanDto;
import com.example.selfstudybe.dtos.Plan.PlanDto;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.models.Plan;
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

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("plan")
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

//    @GetMapping("date")
//    @Operation(summary = "Get user's plan on date")
//    public ResponseEntity<List<PlanDto>> getPlansOnDate(@RequestParam UUID id, @RequestParam LocalDateTime date, BindingResult bindingResult) {
//        List<PlanDto> plans = planService.getUserPlansOnDate(id,date);
//        return ResponseEntity.ok(plans);
//    }

    @PatchMapping("user")
    @Operation(summary = "Update user's plan")
    public ResponseEntity<PlanDto>  updateUserPlan(@Valid @RequestBody PlanDto planDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new CustomBadRequestException(String.join(", ", errors));
        }

        return ResponseEntity.ok(null);
    }

    @DeleteMapping("user")
    @Operation(summary = "Delete user's plan")
    public ResponseEntity<String>  deleteUserPlan(@RequestParam UUID id) {
        planService.deleteUserPlan(id);
        return ResponseEntity.ok("Delete successfully");
    }

    @GetMapping
    public ResponseEntity<List<Plan>> getAllUserPlans() {
        return ResponseEntity.ok(planService.getAllPlans());
    }
}
