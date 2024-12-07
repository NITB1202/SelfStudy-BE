package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.models.PlanUser;
import com.example.selfstudybe.models.PlanUserId;
import com.example.selfstudybe.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlanUserRepository extends JpaRepository<PlanUser, PlanUserId> {
    @EntityGraph(attributePaths = {"plan"})
    List<PlanUser> findByAssignee(User user);
    @EntityGraph(attributePaths = {"assignee"})
    List<PlanUser> findByPlanId(UUID planId);
    boolean existsByPlanIdAndAssigneeId(UUID planId, UUID assigneeId);
    PlanUser findByPlanIdAndAssigneeId(UUID planId, UUID assigneeId);
}