package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Invitation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    List<Invitation> findByUserId(UUID userId);
}