package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.Authentication.AuthRequest;
import com.example.selfstudybe.dtos.Authentication.AuthResponse;
import com.example.selfstudybe.dtos.Authentication.GoogleResponse;
import com.example.selfstudybe.dtos.Authentication.UserInfo;
import com.example.selfstudybe.dtos.User.CreateUserDto;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.security.CustomAuthenticationManager;
import com.example.selfstudybe.services.UserService;
import com.example.selfstudybe.util.JwtUtil;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final UserService userService;
    private final CustomAuthenticationManager authenticationManager;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @PostMapping(value ="login", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Login")
    @ApiResponse(responseCode = "200", description = "Login successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request body")
    public ResponseEntity<AuthResponse> login(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "All fields are required")@Valid @RequestBody AuthRequest authRequest, BindingResult bindingResult,
                                              HttpServletRequest request, HttpServletResponse response ) throws JOSEException {
        // If inputs are invalid
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new CustomBadRequestException(String.join(", ", errors));
        }

        String accessToken = JwtUtil.extractAccessTokenFromCookie(request);

        // If access token already exists in cookie
        if(accessToken != null && JwtUtil.validateAccessToken(accessToken))
            return ResponseEntity.ok(new AuthResponse(accessToken));

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

        // Generate tokens
        String newAccessToken = JwtUtil.generateAccessToken(user.getId(),user.getEmail(),user.getRole().toString());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());

        // Generate cookies
        Cookie accessCookie = JwtUtil.generateCookie("access_token", newAccessToken);
        Cookie refreshCookie = JwtUtil.generateCookie("refresh_token", refreshToken);

        response.addCookie(accessCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new AuthResponse(newAccessToken));
    }

    @GetMapping
    @Operation(summary = "Get current login user's information")
    @ApiResponse(responseCode = "200", description = "Get successfully")
    public ResponseEntity<?> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getPrincipal().toString();
        if(email.equals("anonymousUser"))
            return ResponseEntity.ok("No user has logged in");
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @GetMapping("logout")
    @Operation(summary = "Log out of the current account")
    @ApiResponse(responseCode = "200", description = "Logout successfully")
    public ResponseEntity<?> logout(HttpServletResponse response) {
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

    @GetMapping("login/google")
    @Operation(summary = "Login with google account")
    @ApiResponse(responseCode = "200", description = "Get google authentication url")
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
    public ResponseEntity<?> oauth2Callback(@RequestParam("code") String code){
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
        GoogleResponse response = restTemplate.postForObject(uriBuilder.toUriString(), null, GoogleResponse.class);

        if(response!= null) {
            // Extract access token from response
            String accessToken = response.getAccess_token();
            UserInfo userInfo = getUserInfoFromGoogle(accessToken);
            String email = userInfo.getEmail();

            // Get full information
            User user = userService.getUserByEmail(email);

            if(user == null)
                throw new CustomBadRequestException("This user hasn't registered yet");

            //Set authentication
            List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword(),roles);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Generate tokens
            // Generate cookies

            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Can't find access token");
    }

    private UserInfo getUserInfoFromGoogle(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(userInfoUrl + "?access_token=" + accessToken, UserInfo.class);
    }
}
