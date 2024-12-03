package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Document;
import com.example.selfstudybe.models.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentRepository extends JpaRepository<Document, UUID> {
    List<Document> findBySubject(Subject subject);
    boolean existsByNameAndSubject(String name, Subject subject);
    boolean existsBySubjectId(UUID id);
}