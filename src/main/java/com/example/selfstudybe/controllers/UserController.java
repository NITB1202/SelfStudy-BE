package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.User.CreateUserDto;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @GetMapping("email")
    public ResponseEntity<User> getUser(@Valid @RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PostMapping
    public ResponseEntity<User> CreateUser(@Valid @RequestBody CreateUserDto createUserDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
            throw new CustomBadRequestException(String.join(", ", errors));
        }

        User user = userService.createUser(createUserDto);
        return ResponseEntity.ok(user);
    }

}
