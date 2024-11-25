package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}