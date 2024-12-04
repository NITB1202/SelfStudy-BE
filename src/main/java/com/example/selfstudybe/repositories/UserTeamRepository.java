package com.example.selfstudybe.repositories;

import com.example.selfstudybe.enums.TeamRole;
import com.example.selfstudybe.models.UserTeam;
import com.example.selfstudybe.models.UserTeamId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserTeamRepository extends JpaRepository<UserTeam, UserTeamId> {
    @EntityGraph(attributePaths = {"team"})
    List<UserTeam> findByUserId(UUID userId);
    @EntityGraph(attributePaths = {"user"})
    List<UserTeam> findByTeamId(UUID teamId);
    @EntityGraph(attributePaths = {"user"})
    List<UserTeam> findByTeamIdAndRole(UUID teamId, TeamRole role);
}