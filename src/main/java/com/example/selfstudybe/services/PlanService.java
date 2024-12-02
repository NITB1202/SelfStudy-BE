package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Plan.CreateUserPlanDto;
import com.example.selfstudybe.dtos.Plan.PlanDto;
import com.example.selfstudybe.dtos.Plan.UpdatePlanDto;
import com.example.selfstudybe.enums.PlanStatus;
import com.example.selfstudybe.enums.TaskStatus;
import com.example.selfstudybe.exception.CustomBadRequestException;
import com.example.selfstudybe.exception.CustomNotFoundException;
import com.example.selfstudybe.models.*;
import com.example.selfstudybe.repositories.*;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
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

    public PlanDto createUserPlan(CreateUserPlanDto plan) {
        // Validate
        User user = userRepository.findById(plan.getUserId()).orElse(null);

        if(user == null)
            throw new CustomNotFoundException("Can't find user with id " + plan.getUserId());

        if(plan.getStartDate().isBefore(LocalDateTime.now()))
            throw new CustomBadRequestException("The start date cannot be in the past.");

        if(plan.getNotifyBefore() != null)
        {
            Duration duration = Duration.between(plan.getStartDate(), plan.getEndDate());
            Duration notifyBeforeDuration = Duration.between(LocalTime.MIN, plan.getNotifyBefore());

            if(notifyBeforeDuration.compareTo(duration) > 0)
                throw new CustomBadRequestException("Notify time can't be greater than duration between start and end dates");
        }

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        Plan savedPlan = planRepository.save(modelMapper.map(plan, Plan.class));

        PlanUserId planUserId = new PlanUserId();
        planUserId.setPlanId(savedPlan.getId());
        planUserId.setAssigneeId(plan.getUserId());

        PlanUser planUser = new PlanUser();
        planUser.setId(planUserId);
        planUser.setPlan(savedPlan);
        planUser.setAssignee(user);

        planUserRepository.save(planUser);

        return new ModelMapper().map(savedPlan, PlanDto.class);
    }

    public List<PlanDto> getUserPlansOnDate(UUID userId, LocalDate date) {
        // Find user
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + userId));

        // Get all user's plans(include team's plans)
        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();

        // Get all plans on that date
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);

        List<Plan> datePlans = new ArrayList<>();
        for (Plan plan : plans) {
            if ((startDate.isAfter(plan.getStartDate()) || startDate.isEqual(plan.getStartDate())) &&
                    (endDate.isBefore(plan.getEndDate()) || endDate.isEqual(plan.getEndDate())))
                datePlans.add(plan);
        }

        return new ModelMapper().map(datePlans, new TypeToken<List<PlanDto>>() {}.getType());
    }

    public List<PlanDto> getUserMissedPlans(UUID userId) {
        // Back in 3 days
        LocalDate checkDate = LocalDate.now().minusDays(3);
        LocalDateTime checkTime = checkDate.atStartOfDay();

        // Find user
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + userId));

        // Get all user's plans(include team's plans)
        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();

        List<PlanDto> response = new ArrayList<>();

        for (Plan plan : plans) {
            if(plan.getStatus().equals(PlanStatus.INCOMPLETE)
                    && plan.getEndDate().isBefore(LocalDateTime.now())
                    && plan.getEndDate().isAfter(checkTime))
                response.add(new ModelMapper().map(plan, PlanDto.class));
        }

        return response;
    }

    public PlanDto updatePlan(UpdatePlanDto updatedPlan) {
        //Validate
        Plan plan = planRepository.findById(updatedPlan.getPlanId()).orElseThrow(
                () -> new CustomNotFoundException("Can't find plan with id " + updatedPlan.getPlanId()));

        LocalDate checkDate = LocalDate.now().minusDays(3);
        LocalDateTime checkTime = checkDate.atStartOfDay();
        if(plan.getEndDate().isBefore(checkTime))
            throw new CustomBadRequestException("Can't update a plan after 3 days from when the plan has ended");

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);

        modelMapper.map(updatedPlan, plan);
        planRepository.save(plan);

        return modelMapper.map(plan, PlanDto.class);
    }

    public void deletePlan(UUID planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomNotFoundException("Can't find plan with id " + planId));
        planRepository.delete(plan);
    }
}
