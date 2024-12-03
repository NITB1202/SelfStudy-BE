package com.example.selfstudybe.dtos.Subject;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class SubjectDto {
    private UUID subjectId;
    private String name;
    private String imageLink;
    private UUID creatorId;
    private Boolean isPersonal;

}
