package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.models.Team;
import com.example.selfstudybe.models.TeamPlan;
import com.example.selfstudybe.models.TeamPlanId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TeamPlanRepository extends JpaRepository<TeamPlan, TeamPlanId> {
    TeamPlan findByPlan(Plan plan);
}