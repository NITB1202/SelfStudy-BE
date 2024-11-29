package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.User.CreateUserDto;
import com.example.selfstudybe.dtos.User.UpdateUserDto;
import com.example.selfstudybe.dtos.User.UserDto;
import com.example.selfstudybe.enums.Role;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.UserService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("v1/user")
public class UserController {
    private final UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create a new user")
    @ApiResponse(responseCode = "200", description = "Create successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<UserDto> CreateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are required")
                                                  @Valid @RequestBody CreateUserDto createUserDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new CustomBadRequestException(String.join(", ", errors));
        }

        return ResponseEntity.ok(userService.createUser(createUserDto));
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Find all users that match the query parameters")
    @ApiResponse(responseCode = "200", description = "Find successfully")
    public ResponseEntity<List<UserDto>> searchUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are optional")
                                                        @RequestParam(required = false) String email,
                                                        @RequestParam(required = false) String username,
                                                        @RequestParam(required = false) Role role) {
        List<UserDto> users = userService.searchUsers(email, username, role);
        return ResponseEntity.ok(users);
    }

    @PatchMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update user's information")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<UserDto> updateUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are optional")
                                                  @Valid @RequestBody UpdateUserDto user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new CustomBadRequestException(String.join(", ", errors));
        }

        return ResponseEntity.ok(userService.updateUser(user));
    }

    @PostMapping(value = "avatar/{id}", consumes = "multipart/form-data")
    @Operation(summary = "Upload user's avatar")
    @ApiResponse(responseCode = "200", description = "Upload successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<String> uploadAvatar(@PathVariable UUID id, @RequestParam("file") MultipartFile file) throws IOException {
        String url = userService.uploadAvatar(id,file);
        return ResponseEntity.ok(url);
    }
}
