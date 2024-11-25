package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.TeamPlan;
import com.example.selfstudybe.models.TeamPlanId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamPlanRepository extends JpaRepository<TeamPlan, TeamPlanId> {
}