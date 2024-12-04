package com.example.selfstudybe.dtos.Team;

import com.example.selfstudybe.enums.TeamRole;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
@Getter @Setter
public class TeamMemberDto {
    private UUID userId;

    private String email;

    private String username;

    private String avatarLink;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private TeamRole teamRole;
}
