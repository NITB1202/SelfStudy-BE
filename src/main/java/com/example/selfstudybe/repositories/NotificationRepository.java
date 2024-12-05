package com.example.selfstudybe.repositories;

import com.example.selfstudybe.models.Notification;
import com.example.selfstudybe.models.User;
import org.aspectj.weaver.ast.Not;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findByUser(User user);
}