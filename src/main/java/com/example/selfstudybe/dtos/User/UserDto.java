package com.example.selfstudybe.dtos.User;

import com.example.selfstudybe.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Setter
public class UserDto implements Serializable {
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private UUID id;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String email;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private String avatarLink;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Role role;
}