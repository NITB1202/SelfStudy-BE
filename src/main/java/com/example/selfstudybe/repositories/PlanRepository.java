package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
}