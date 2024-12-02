package com.example.selfstudybe.dtos.Subject;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@Getter @Setter
public class CreateUserSubjectDto {
    private UUID userId;
    private String name;
    private String imageLink;
}
