package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
}