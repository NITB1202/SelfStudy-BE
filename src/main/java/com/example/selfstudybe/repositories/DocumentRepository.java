package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Document;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
}