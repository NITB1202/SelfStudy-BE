package com.example.selfstudybe.repositories;

import com.example.selfstudybe.enums.SessionStatus;
import com.example.selfstudybe.models.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StudySessionRepository extends JpaRepository<StudySession, UUID> {
    List<StudySession> findByUserIdAndStatus(UUID userId, SessionStatus status);
}