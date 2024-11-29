package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Plan.CreateUserPlanDto;
import com.example.selfstudybe.dtos.Plan.PlanDto;
import com.example.selfstudybe.enums.PlanStatus;
import com.example.selfstudybe.enums.TaskStatus;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.*;
import com.example.selfstudybe.repositories.PlanRepository;
import com.example.selfstudybe.repositories.PlanUserRepository;
import com.example.selfstudybe.repositories.TaskRepository;
import com.example.selfstudybe.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PlanService {
    private final PlanRepository planRepository;
    private final UserRepository userRepository;
    private final PlanUserRepository planUserRepository;
    private final TaskRepository taskRepository;

    public PlanDto createUserPlan(CreateUserPlanDto plan) {
        // Validate
        User user = userRepository.findById(plan.getUserId()).orElse(null);

        if(user == null)
            throw new CustomNotFoundException("Can't find user with id " + plan.getUserId());

        if(plan.getStartDate().isBefore(LocalDateTime.now()))
            throw new CustomBadRequestException("The start date cannot be in the past.");

        if(plan.getStartDate().isAfter(plan.getEndDate()) || plan.getEndDate().isEqual(plan.getStartDate()))
            throw new CustomBadRequestException("Start date can't be equal or after end date");

        if(plan.getNotifyBefore() != null)
        {
            Duration duration = Duration.between(plan.getStartDate(), plan.getEndDate());
            Duration notifyBeforeDuration = Duration.between(LocalTime.MIN, plan.getNotifyBefore());

            if(notifyBeforeDuration.compareTo(duration) > 0)
                throw new CustomBadRequestException("Notify time can't be greater than duration between start and end dates");
        }

        Plan newPlan = new Plan();
        newPlan.setName(plan.getName());
        newPlan.setStartDate(plan.getStartDate());
        newPlan.setEndDate(plan.getEndDate());
        if(plan.getNotifyBefore() != null)
            newPlan.setNotifyBefore(plan.getNotifyBefore());

        newPlan.setStatus(PlanStatus.INCOMPLETE);

        Plan savedPlan = planRepository.save(newPlan);

        PlanUserId planUserId = new PlanUserId();
        planUserId.setPlanId(savedPlan.getId());
        planUserId.setAssigneeId(plan.getUserId());

        PlanUser planUser = new PlanUser();
        planUser.setId(planUserId);
        planUser.setPlan(savedPlan);
        planUser.setAssignee(user);

        planUserRepository.save(planUser);

        PlanDto response = new ModelMapper().map(savedPlan, PlanDto.class);
        response.setProcess(0);
        response.setAssigned(true);

        return response;
    }

    public List<PlanDto> getUserPlansOnDate(UUID userId, LocalDateTime date) {
        // Find user
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + userId));

        // Get all user's plans(include team's plans)
        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();

        // Get all plans on that date
        List<Plan> datePlans = new ArrayList<>();
        for (Plan plan : plans) {
            if ((date.isBefore(plan.getStartDate()) || date.isEqual(plan.getStartDate())) &&
                    (date.isAfter(plan.getEndDate()) || date.isEqual(plan.getEndDate())))
                datePlans.add(plan);
        }

        // Calculate plan's progress
        List<PlanDto> response = new ArrayList<>();

        for (Plan plan : datePlans) {
            List<Task> tasks = taskRepository.findByPlan(plan);

            int finishedTasks = 0;
            int totalTasks = tasks.size();

            for (Task task : tasks)
                if (task.getStatus().equals(TaskStatus.COMPLETED))
                    finishedTasks++;

            double process = totalTasks != 0 ? (double) finishedTasks / totalTasks : 0;

            PlanDto planDto = new ModelMapper().map(plan, PlanDto.class);
            planDto.setProcess(process);
            planDto.setAssigned(true);

            response.add(planDto);
        }

        return response;
    }

    public void deleteUserPlan(UUID planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + planId));
        planRepository.delete(plan);
    }

    public List<Plan> getAllPlans()
    {
        return planRepository.findAll();
    }
}
