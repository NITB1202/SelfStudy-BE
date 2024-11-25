package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.PlanUser;
import com.example.selfstudybe.models.PlanUserId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanUserRepository extends JpaRepository<PlanUser, PlanUserId> {
}