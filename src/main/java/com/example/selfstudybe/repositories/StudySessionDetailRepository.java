package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.StudySessionDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudySessionDetailRepository extends JpaRepository<StudySessionDetail, UUID> {
}