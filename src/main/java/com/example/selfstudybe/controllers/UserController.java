package com.example.selfstudybe.controllers;

import com.example.selfstudybe.dtos.User.UserDto;
import com.example.selfstudybe.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("user")
public class UserController {
    private final UserService userService;

    @GetMapping("email")
    public ResponseEntity<UserDto> getUser(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
}
