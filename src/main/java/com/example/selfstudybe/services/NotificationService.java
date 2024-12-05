package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Notification.CreateNotificationDto;
import com.example.selfstudybe.dtos.Notification.NotificationDto;
import com.example.selfstudybe.dtos.Plan.PlanDto;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.Notification;
import com.example.selfstudybe.models.PlanUser;
import com.example.selfstudybe.models.PlanUserId;
import com.example.selfstudybe.models.User;
import com.example.selfstudybe.repositories.NotificationRepository;
import com.example.selfstudybe.repositories.PlanRepository;
import com.example.selfstudybe.repositories.PlanUserRepository;
import com.example.selfstudybe.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Hibernate;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PlanUserRepository planUserRepository;

    @Transactional
    public NotificationDto saveNotification(CreateNotificationDto notificationDto) {
        PlanUserId id = new PlanUserId();
        id.setAssigneeId(notificationDto.getUserId());
        id.setPlanId(notificationDto.getPlanId());

        PlanUser planUser = planUserRepository.findById(id).orElseThrow(
                () -> new CustomBadRequestException("This user isn't assigned to this plan")
        );

        Hibernate.initialize(planUser.getPlan());
        Hibernate.initialize(planUser.getAssignee());

        Notification notification = new Notification();
        notification.setPlan(planUser.getPlan());
        notification.setUser(planUser.getAssignee());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        notification.setType(notificationDto.getType());

        return modelMapper.map(notificationRepository.save(notification), NotificationDto.class);
    }

    public List<NotificationDto> getAllNotifications(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find user with id " + userId)
        );

        List<Notification> notifications = notificationRepository.findByUser(user);
        return modelMapper.map(notifications, new TypeToken<List<NotificationDto>>() {}.getType());
    }

    public NotificationDto readNotification(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElseThrow(
                ()-> new CustomNotFoundException("Can't find notification with id " + notificationId)
        );

        notification.setRead(true);
        notificationRepository.save(notification);

        return modelMapper.map(notification, NotificationDto.class);
    }
}
