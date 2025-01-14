package com.example.selfstudybe.security;

import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.UserRepository;
import com.example.selfstudybe.util.JwtUtil;
import com.nimbusds.jose.JOSEException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CustomFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = JwtUtil.extractAccessTokenFromCookie(request);
        if(accessToken != null && JwtUtil.validateAccessToken(accessToken))
        {
            // If access token is valid
            Jwt jwt = JwtUtil.getJwtDecoder().decode(accessToken);
            String userId = jwt.getSubject();

            User user = userRepository.findById(UUID.fromString(userId)).orElse(null);

            if(user == null)
                throw new CustomNotFoundException("Can't find user with id " + userId);

            generateAuthenticationFromUser(user);
        }

        String refreshToken = JwtUtil.extractRefreshTokenFromCookie(request);
        if(refreshToken != null && JwtUtil.validateRefreshToken(refreshToken))
        {
            // If refresh token is valid
            Jwt jwt = JwtUtil.getJwtDecoder().decode(refreshToken);
            String userId = jwt.getSubject();

            User user = userRepository.findById(UUID.fromString(userId)).orElse(null);
            if(user == null)
                throw new CustomNotFoundException("Can't find user with id " + userId);

            try {
                String newAccessToken = JwtUtil.generateAccessToken(UUID.fromString(userId),user.getEmail(),user.getRole().toString());
                Cookie cookie = JwtUtil.generateCookie("access_token", newAccessToken);
                response.addCookie(cookie);
            } catch (JOSEException e) {
                throw new CustomBadRequestException("Can't generate access token");
            }

            generateAuthenticationFromUser(user);
        }

        filterChain.doFilter(request, response);
    }

    private void generateAuthenticationFromUser(User user)
    {
        List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword(),roles);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
