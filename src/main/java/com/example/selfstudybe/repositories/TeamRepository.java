package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Team;
import com.example.selfstudybe.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<Team, UUID> {
    boolean existsByNameAndCreator(String name, User creator);
}