package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Authentication.Request.AuthRequest;
import com.example.selfstudybe.dtos.Authentication.Request.VerificationRequest;
import com.example.selfstudybe.dtos.Authentication.Response.AuthResponse;
import com.example.selfstudybe.dtos.Authentication.Response.GoogleResponse;
import com.example.selfstudybe.dtos.Authentication.Response.UserInfo;
import com.example.selfstudybe.enums.Role;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.exception.ErrorResponse;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.UserRepository;
import com.example.selfstudybe.security.CustomAuthenticationManager;
import com.example.selfstudybe.services.EmailService;
import com.example.selfstudybe.services.UserService;
import com.example.selfstudybe.util.JwtUtil;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final CustomAuthenticationManager authenticationManager;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final int waitMinutes = 2;
    private final UserRepository userRepository;

    @PostMapping(value ="login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Login successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are required")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest, HttpServletRequest request, HttpServletResponse response ) throws JOSEException {

        String accessToken = JwtUtil.extractAccessTokenFromCookie(request);

        // If access token already exists in cookie
        if(accessToken != null && JwtUtil.validateAccessToken(accessToken))
        {
            String refreshToken = JwtUtil.extractRefreshTokenFromCookie(request);
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        }

        // If access token don't exist, then create a new one
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword());

        // Validate user
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // Add authentication to security context;
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Get user's full information
        String email = authentication.getPrincipal().toString();
        User user = userService.getUserByEmail(email);

        // Generate cookies
        AuthResponse authResponse = generateCookies(user,response);

        return ResponseEntity.ok(authResponse);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get current login user's information")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    @ApiResponse(responseCode = "404", description = "Not found", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<User> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        if(email.equals("anonymousUser"))
            throw new CustomNotFoundException("No user has logged in");
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("logout")
    @Operation(summary = "Log out of the current account")
    @ApiResponse(responseCode = "200", description = "Logout successfully")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        Cookie accessTokenCookie = new Cookie("access_token", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("refresh_token", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logout successful");
    }

    @GetMapping(value = "google")
    @Operation(summary = "Get google authentication URL")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<String> loginWithGoogle() {
        // Extract client's information
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");

        // Create Url
        String authorizationUri = clientRegistration.getProviderDetails().getAuthorizationUri()
                + "?response_type=code"
                + "&client_id=" + clientRegistration.getClientId()
                + "&redirect_uri=" + clientRegistration.getRedirectUri()
                + "&scope=" + String.join(" ", clientRegistration.getScopes());

        return ResponseEntity.ok(authorizationUri);
    }

    @Hidden
    @GetMapping("oauth2callback")
    public ResponseEntity<String> oauth2Callback(@RequestParam("code") String code){
        return ResponseEntity.ok("Authorization code: "+ code);
    }

    @PostMapping(value= "code", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login with google authorization code")
    @ApiResponse(responseCode = "200", description = "Login successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    @ApiResponse(responseCode = "404", description = "Not exists", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody String code, HttpServletResponse response) throws JOSEException {
        if(code == null || code.isBlank())
            throw new CustomBadRequestException("Invalid code");

        // Extract client's information
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");

        // Create URL to exchange authorization code with access token
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tokenUri)
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", code)
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("client_secret", clientRegistration.getClientSecret())
                .queryParam("redirect_uri", clientRegistration.getRedirectUri());

        RestTemplate restTemplate = new RestTemplate();
        GoogleResponse googleResponse = restTemplate.postForObject(uriBuilder.toUriString(), null, GoogleResponse.class);

        if(googleResponse!= null) {
            // Extract access token from response
            String accessToken = googleResponse.getAccess_token();

            // Get user's information
            UserInfo userInfo = getUserInfoFromGoogle(accessToken);
            String email = userInfo.getEmail();
            User user = userRepository.findByEmail(email);
            if(user == null) {
                // Create new user with given email
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setUsername(userInfo.getName());
                newUser.setRole(Role.USER);
                newUser.setUsage(0f);

                userRepository.save(newUser);

                user = newUser;
            }

            // Set authentication
            List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(), roles);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate cookies
            AuthResponse authResponse = generateCookies(user,response);

            return ResponseEntity.ok(authResponse);
        }

        throw new CustomBadRequestException("Invalid authorization code");
    }

    @GetMapping("mail")
    @Operation(summary = "Send a verification code to the user's email address")
    @ApiResponse(responseCode = "200", description = "Send successfully")
    @ApiResponse(responseCode = "400", description = "Invalid format", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<String> sendVerificationCode(@Valid @RequestParam @Email String email) {
        // Generate random code
        String code = generateRandomCode();

        // Save code into Redis database
        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(waitMinutes));

        // Send verification to user's email address
        emailService.sendMail(email,"Verification", "Your verification code is " + code);
        return ResponseEntity.ok("Verification code sent");
    }

    @PostMapping("verify")
    @Operation(summary = "Verify authentication")
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "400", description = "Fail", content =
            { @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)) })
    public ResponseEntity<String> verify(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "All fields are required")
                                             @Valid @RequestBody VerificationRequest verificationRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new CustomBadRequestException(String.join(", ", errors));
        }

        String storedCode = redisTemplate.opsForValue().get(verificationRequest.getEmail());
        if(storedCode != null && storedCode.equals(verificationRequest.getCode())) {
            redisTemplate.delete(verificationRequest.getEmail());
            return ResponseEntity.ok("Verification successful");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Verification failed");
    }

    private String generateRandomCode() {
        int randomNum = 1000 + new Random().nextInt(9000);
        return String.valueOf(randomNum);
    }

    private UserInfo getUserInfoFromGoogle(String accessToken) {
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(userInfoUrl + "?access_token=" + accessToken, UserInfo.class);
    }

    private AuthResponse generateCookies(User user, HttpServletResponse response) throws JOSEException {
        // Generate tokens
        String accessToken = JwtUtil.generateAccessToken(user.getId(),user.getEmail(),user.getRole().toString());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());

        // Generate cookies
        Cookie accessCookie = JwtUtil.generateCookie("access_token", accessToken);
        Cookie refreshCookie = JwtUtil.generateCookie("refresh_token", refreshToken);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return new AuthResponse(accessToken, refreshToken);
    }
}
