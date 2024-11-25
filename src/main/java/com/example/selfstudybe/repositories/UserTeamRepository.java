package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.UserTeam;
import com.example.selfstudybe.models.UserTeamId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTeamRepository extends JpaRepository<UserTeam, UserTeamId> {
}