package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Plan;
import com.example.selfstudybe.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByPlan(Plan plan);
    boolean existsByNameAndPlan(String name, Plan plan);
}