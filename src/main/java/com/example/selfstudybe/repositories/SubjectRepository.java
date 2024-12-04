package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Subject;
import com.example.selfstudybe.models.Team;
import com.example.selfstudybe.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface SubjectRepository extends JpaRepository<Subject, UUID> {
    boolean existsByNameAndCreatorAndIsPersonal(String name, User user, Boolean isPersonal);
    List<Subject> findByCreatorAndIsPersonal(User user, Boolean isPersonal);
}