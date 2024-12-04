package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Team;
import com.example.selfstudybe.models.TeamSubject;
import com.example.selfstudybe.models.TeamSubjectId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamSubjectRepository extends JpaRepository<TeamSubject, TeamSubjectId> {
    @EntityGraph(attributePaths = {"subject"})
    List<TeamSubject> findByTeam(Team team);
}