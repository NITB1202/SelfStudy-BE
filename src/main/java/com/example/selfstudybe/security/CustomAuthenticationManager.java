package com.example.selfstudybe.security;

import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CustomAuthenticationManager implements AuthenticationManager {
    private final UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        User user = userService.getUserByEmail(username);
        if(user == null)
            throw new CustomNotFoundException("Can't find user with email: " + username);

        if(user.getPassword() == null)
            throw new CustomBadRequestException("This user has registered with google account");

        if(passwordEncoder.matches(password, user.getPassword())) {
            List<GrantedAuthority> roles = List.of(new SimpleGrantedAuthority(user.getRole().toString()));
            return new UsernamePasswordAuthenticationToken(username, password, roles);
        }

        throw new CustomNotFoundException("Incorrect password");
    }
}
