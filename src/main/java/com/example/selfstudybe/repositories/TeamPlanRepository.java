package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.models.Team;
import com.example.selfstudybe.models.TeamPlan;
import com.example.selfstudybe.models.TeamPlanId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamPlanRepository extends JpaRepository<TeamPlan, TeamPlanId> {
    @EntityGraph(attributePaths = {"plan"})
    List<TeamPlan> findByTeam(Team team);
    @EntityGraph(attributePaths = {"team"})
    TeamPlan findByPlan(Plan plan);
}