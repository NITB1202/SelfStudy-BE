package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Authentication.Request.AuthRequest;
import com.example.selfstudybe.dtos.Authentication.Request.VerificationRequest;
import com.example.selfstudybe.dtos.Authentication.Response.AuthResponse;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.services.AuthService;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@AllArgsConstructor
public class AuthenticationController {
    private final AuthService authService;

    @PostMapping(value ="login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Login successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are required")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) throws JOSEException {
        return ResponseEntity.ok(authService.login(authRequest));
    }

    @GetMapping(value = "google")
    @Operation(summary = "Get google authentication URL")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<String> getGoogleAuthenticationUrl(HttpServletRequest request) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        String fullRedirectUrl = baseUrl + "/oauth2/authorization/google";
        return ResponseEntity.ok(fullRedirectUrl);
    }

    @Hidden
    @GetMapping("oauth2callback")
    public ResponseEntity<AuthResponse> oauth2Callback(@RequestParam("code") String code) throws JOSEException {
        return ResponseEntity.ok(authService.loginWithGoogle(code));
    }

    @GetMapping("mail")
    @Operation(summary = "Send a verification code to the user's email address")
    @ApiResponse(responseCode = "200", description = "Send successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestParam @Email String email) {
        String code = authService.sendVerificationCode(email);
        return ResponseEntity.ok("Verification code: " + code);
    }

    @PostMapping("verify")
    @Operation(summary = "Verify authentication")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "400", description = "Fail", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<String> verify(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are required")
                                             @Valid @RequestBody VerificationRequest verificationRequest) {
        if(authService.verifyCode(verificationRequest))
            return ResponseEntity.ok("Verification successful");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed");
    }

    @PostMapping("reset")
    @Operation(summary = "Change user's password")
    @ApiResponse(responseCode = "200", description = "Update successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<String> resetPassword(@Valid @RequestBody AuthRequest request) {
        authService.resetPassword(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("Password reset successful");
    }
}
