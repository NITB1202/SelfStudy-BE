package com.example.selfstudybe.dtos.User;

import com.example.selfstudybe.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

public record UserDto(String email, String username, String avatarLink,
                      @JsonFormat(shape = JsonFormat.Shape.STRING) Role role) implements Serializable {
}