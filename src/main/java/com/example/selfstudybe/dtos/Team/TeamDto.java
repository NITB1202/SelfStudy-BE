package com.example.selfstudybe.dtos.Team;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class TeamDto {
    private UUID id;

    private UUID creatorId;

    private String name;

    private String imageLink;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    private Integer num;

    private Float usage;
}
