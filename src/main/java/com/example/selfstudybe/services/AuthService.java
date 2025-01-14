package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Authentication.Request.AuthRequest;
import com.example.selfstudybe.dtos.Authentication.Request.VerificationRequest;
import com.example.selfstudybe.dtos.Authentication.Response.AuthResponse;
import com.example.selfstudybe.dtos.Authentication.Response.GoogleResponse;
import com.example.selfstudybe.dtos.Authentication.Response.UserInfo;
import com.example.selfstudybe.enums.Role;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.UserRepository;
import com.example.selfstudybe.security.CustomAuthenticationManager;
import com.example.selfstudybe.util.JwtUtil;
import com.nimbusds.jose.JOSEException;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class AuthService {
    private final CustomAuthenticationManager authenticationManager;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final EmailService emailService;
    private final RedisTemplate<String, String> redisTemplate;
    private final UserRepository userRepository;

    public AuthResponse login(AuthRequest request) throws JOSEException {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail());
        String accessToken = JwtUtil.generateAccessToken(user.getId(),user.getEmail(),user.getRole().toString());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse loginWithGoogle(String authorizationCode) throws JOSEException {
        // Create URL to exchange authorization code with access token
        ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId("google");
        String tokenUri = clientRegistration.getProviderDetails().getTokenUri();

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(tokenUri)
                .queryParam("grant_type", "authorization_code")
                .queryParam("code", authorizationCode)
                .queryParam("client_id", clientRegistration.getClientId())
                .queryParam("client_secret", clientRegistration.getClientSecret())
                .queryParam("redirect_uri", clientRegistration.getRedirectUri());

        RestTemplate restTemplate = new RestTemplate();
        GoogleResponse googleResponse = restTemplate.postForObject(uriBuilder.toUriString(), null, GoogleResponse.class);

        if(googleResponse == null)
            throw new CustomBadRequestException("Google response from authorization code is null.");

        // Extract access token from response
        String googleAccessToken = googleResponse.getAccess_token();

        // Get user's information
        UserInfo userInfo = getUserInfoFromGoogle(googleAccessToken);
        String email = userInfo.getEmail();
        User user = userRepository.findByEmail(email);

        // If user hasn't registered yet, create a new account
        if (user == null) {
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

        String accessToken = JwtUtil.generateAccessToken(user.getId(),user.getEmail(),user.getRole().toString());
        String refreshToken = JwtUtil.generateRefreshToken(user.getId());

        return new AuthResponse(accessToken, refreshToken);
    }

    public String sendVerificationCode(String email){
        if(userRepository.findByEmail(email) == null)
            throw new CustomBadRequestException("Can't find user with email "+ email);

        int waitMinutes = 2;
        String code = generateRandomCode();

        redisTemplate.opsForValue().set(email, code, Duration.ofMinutes(waitMinutes));
        emailService.sendMail(email,"Verification", "Your verification code is " + code);

        return code;
    }

    public boolean verifyCode(VerificationRequest request){
        String storedCode = redisTemplate.opsForValue().get(request.getEmail());
        if(storedCode != null && storedCode.equals(request.getCode())) {
            redisTemplate.delete(request.getEmail());
            return true;
        }

        return false;
    }

    private UserInfo getUserInfoFromGoogle(String accessToken) {
        String userInfoUrl = "https://www.googleapis.com/oauth2/v3/userinfo";
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(userInfoUrl + "?access_token=" + accessToken, UserInfo.class);
    }

    private String generateRandomCode() {
        int randomNum = 1000 + new Random().nextInt(9000);
        return String.valueOf(randomNum);
    }
}
