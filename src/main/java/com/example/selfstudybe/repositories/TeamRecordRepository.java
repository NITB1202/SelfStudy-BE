package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.TeamRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRecordRepository extends JpaRepository<TeamRecord, UUID> {
}