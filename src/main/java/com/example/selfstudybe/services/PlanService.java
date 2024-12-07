package com.example.selfstudybe.services;

import com.example.selfstudybe.dtos.Plan.*;
import com.example.selfstudybe.enums.PlanStatus;
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
    private final TeamRepository teamRepository;
    private final TeamPlanRepository teamPlanRepository;
    private final ModelMapper modelMapper;
    private final UserTeamRepository userTeamRepository;

    public PlanDto createUserPlan(CreateUserPlanDto request) {
        // Validate plan
        User user = userRepository.findById(request.getUserId()).orElseThrow(
                ()->new CustomBadRequestException("Can't find user with id " + request.getUserId())
        );

        if(request.getEndDate().isBefore(LocalDateTime.now()))
            throw new CustomBadRequestException("The end date cannot be in the past.");

        if(request.getNotifyBefore() != null)
        {
            Duration duration = Duration.between(request.getStartDate(), request.getEndDate());
            Duration notifyBeforeDuration = Duration.between(LocalTime.MIN, request.getNotifyBefore());

            if(notifyBeforeDuration.compareTo(duration) > 0)
                throw new CustomBadRequestException("Notify time can't be greater than duration between start and end dates");
        }

        Plan savedPlan = planRepository.save(modelMapper.map(request, Plan.class));

        PlanUserId planUserId = new PlanUserId();
        planUserId.setPlanId(savedPlan.getId());
        planUserId.setAssigneeId(request.getUserId());

        PlanUser planUser = new PlanUser();
        planUser.setId(planUserId);
        planUser.setAssignee(user);
        planUser.setPlan(savedPlan);

        planUserRepository.save(planUser);

        return modelMapper.map(savedPlan, PlanDto.class);
    }

    public PlanDto createTeamPlan(CreateTeamPlanDto request) {
        Team team = teamRepository.findById(request.getTeamId()).orElseThrow(
                ()->new CustomBadRequestException("Can't find team with id " + request.getTeamId())
        );

        if(request.getEndDate().isBefore(LocalDateTime.now()))
            throw new CustomBadRequestException("The end date cannot be in the past.");

        if(request.getNotifyBefore() != null)
        {
            Duration duration = Duration.between(request.getStartDate(), request.getEndDate());
            Duration notifyBeforeDuration = Duration.between(LocalTime.MIN, request.getNotifyBefore());

            if(notifyBeforeDuration.compareTo(duration) > 0)
                throw new CustomBadRequestException("Notify time can't be greater than duration between start and end dates");
        }

        Plan plan = modelMapper.map(request, Plan.class);
        plan.setPersonal(false);

        Plan savedPlan = planRepository.save(plan);

        TeamPlanId id = new TeamPlanId();
        id.setPlanId(savedPlan.getId());
        id.setTeamId(request.getTeamId());

        TeamPlan teamPlan = new TeamPlan();
        teamPlan.setId(id);
        teamPlan.setTeam(team);
        teamPlan.setPlan(savedPlan);

        teamPlanRepository.save(teamPlan);

        return modelMapper.map(savedPlan, PlanDto.class);
    }

    public List<PlanDto> getUserPlansOnDate(UUID userId, LocalDate date) {
        // Find user
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + userId));

        // Get all user's plans
        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();
        List<Plan> plansOnDate = filterPlansOnDate(date, plans);

        return modelMapper.map(plansOnDate, new TypeToken<List<PlanDto>>() {}.getType());
    }

    public List<TeamPlanDto> getTeamPlansOnDate(UUID userId, UUID teamId, LocalDate date) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new CustomNotFoundException("Can't find team with id " + teamId)
        );

        if(!userTeamRepository.existsByTeamIdAndUserId(teamId, userId))
            throw new CustomBadRequestException("This user is not in the team");

        List<TeamPlan> teamPlans = teamPlanRepository.findByTeam(team);
        List<Plan> plans = teamPlans.stream().map(TeamPlan::getPlan).toList();
        List<Plan> plansOnDate = filterPlansOnDate(date, plans);
        List<TeamPlanDto> result = new ArrayList<>();

        for(Plan plan : plansOnDate) {
            TeamPlanDto planDto = modelMapper.map(plan, TeamPlanDto.class);
            planDto.setAssigned(planUserRepository.existsByPlanIdAndAssigneeId(plan.getId(), userId));
            result.add(planDto);
        }

        return result;
    }

    public List<PlanDto> getUserMissedPlans(UUID userId) {
        // Find user
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomNotFoundException("Can't find user with id " + userId));

        // Get all user's plans
        List<PlanUser> planUsers = planUserRepository.findByAssignee(user);
        List<Plan> plans = planUsers.stream().map(PlanUser::getPlan).toList();

        return filterMissedPlans(plans);
    }

    public List<PlanDto> getTeamMissedPlans(UUID teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(
                () -> new CustomNotFoundException("Can't find team with id " + teamId)
        );

        List<TeamPlan> teamPlans = teamPlanRepository.findByTeam(team);
        List<Plan> plans = teamPlans.stream().map(TeamPlan::getPlan).toList();

        return filterMissedPlans(plans);
    }

    public PlanDto updatePlan(UpdatePlanDto updatedPlan) {
        //Validate
        Plan plan = planRepository.findById(updatedPlan.getPlanId()).orElseThrow(
                () -> new CustomNotFoundException("Can't find plan with id " + updatedPlan.getPlanId()));

        LocalDate checkDate = LocalDate.now().minusDays(3);
        LocalDateTime checkTime = checkDate.atStartOfDay();
        if(plan.getEndDate().isBefore(checkTime))
            throw new CustomBadRequestException("Can't update a plan after 3 days from when the plan has ended");

        modelMapper.map(updatedPlan, plan);
        planRepository.save(plan);

        return modelMapper.map(plan, PlanDto.class);
    }

    public void deletePlan(UUID planId) {
        Plan plan = planRepository.findById(planId).orElseThrow(() -> new CustomNotFoundException("Can't find plan with id " + planId));
        planRepository.delete(plan);
    }

    public List<Plan> filterPlansOnDate(LocalDate date, List<Plan> plans) {
        LocalDateTime startDate = date.atStartOfDay();
        LocalDateTime endDate = date.atTime(LocalTime.MAX);

        List<Plan> datePlans = new ArrayList<>();

        for (Plan plan : plans) {
            if (isPlanOnDate(startDate, endDate, plan))
                datePlans.add(plan);
        }

        return datePlans;
    }

    public boolean isPlanOnDate(LocalDateTime startDate, LocalDateTime endDate, Plan plan) {
        return !startDate.isAfter(plan.getEndDate()) && !endDate.isBefore(plan.getStartDate());
    }

    public List<PlanDto> filterMissedPlans(List<Plan> plans) {
        // Back in 3 days
        LocalDate checkDate = LocalDate.now().minusDays(3);
        LocalDateTime checkTime = checkDate.atStartOfDay();

        List<PlanDto> response = new ArrayList<>();

        for (Plan plan : plans) {
            if(plan.getStatus().equals(PlanStatus.INCOMPLETE)
                    && plan.getEndDate().isBefore(LocalDateTime.now())
                    && plan.getEndDate().isAfter(checkTime))
                response.add(modelMapper.map(plan, PlanDto.class));
        }

        return response;
    }
}
