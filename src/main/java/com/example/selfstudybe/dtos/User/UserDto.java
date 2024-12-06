package com.example.selfstudybe.dtos.User;

import com.example.selfstudybe.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class UserDto implements Serializable {
    private UUID id;

    private String email;

    private String username;

    private String avatarLink;

    private Role role;

    private Float usage;
}