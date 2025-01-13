package com.example.selfstudybe.controllers;

import com.example.selfstudybe.services.StatisticService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalTime;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("statistic")
public class StatisticController {
    private final StatisticService statisticService;

    @GetMapping("finish")
    @Operation(summary = "Get the number of finished plan in the current week")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<Integer> finishPlans(@RequestParam UUID userId) {
        return ResponseEntity.ok(statisticService.getFinishedPlanInWeek(userId));
    }

    @GetMapping("hour")
    @Operation(summary = "Get total hours spent on the study session in the current week")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<LocalTime> totalHours(@RequestParam UUID userId) {
        return ResponseEntity.ok(statisticService.focusTimeInWeek(userId));
    }

    @GetMapping("finish-rate")
    @Operation(summary = "Get the finished plan's rate in the current week")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<Float> finishRate(@RequestParam UUID userId) {
        return ResponseEntity.ok(statisticService.levelOfCompletion(userId));
    }

    @GetMapping("finish-session")
    @Operation(summary = "Get the finished study session's rate in the current week")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<Float> finishSessionRate(@RequestParam UUID userId) {
        return ResponseEntity.ok(statisticService.levelOfFinishedSession(userId));
    }
}
