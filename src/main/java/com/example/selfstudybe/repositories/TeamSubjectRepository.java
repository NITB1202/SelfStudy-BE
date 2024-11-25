package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.TeamSubject;
import com.example.selfstudybe.models.TeamSubjectId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamSubjectRepository extends JpaRepository<TeamSubject, TeamSubjectId> {
}